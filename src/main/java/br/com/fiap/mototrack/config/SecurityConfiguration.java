package br.com.fiap.mototrack.config;

import br.com.fiap.mototrack.model.Usuario;
import br.com.fiap.mototrack.repository.UsuarioRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    /*
     * # 🔐 PasswordEncoder
     * - **BCrypt** para armazenar senhas com hash seguro.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * # 👤 UserDetailsService
     * - **Objetivo:** carregar o usuário por e-mail e **normalizar** o perfil para authorities do Spring.
     * - **Mapeamento de perfis do domínio → roles do Spring:**
     *   - `ADMINISTRADOR`, `GESTOR` → `ROLE_ADMIN`
     *   - `OPERADOR` ou `null`      → `ROLE_USER`
     */
    @Bean
    UserDetailsService userDetailsService(UsuarioRepository repo) {
        return (String username) -> {
            Usuario u = repo.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

            String p = u.getPerfil() == null ? "USER" : u.getPerfil().trim().toUpperCase();
            if (p.equals("ADMINISTRADOR") || p.equals("GESTOR")) p = "ADMIN";
            if (p.equals("OPERADOR")) p = "USER";

            String role = "ROLE_" + (p.equals("ADMIN") ? "ADMIN" : "USER");
            return new User(u.getEmail(), u.getSenha(), List.of(new SimpleGrantedAuthority(role)));
        };
    }

    /*
     * # 🧩 AuthenticationProvider
     * - **DaoAuthenticationProvider** usando o `UserDetailsService` e `PasswordEncoder`.
     * - **Cache opcional** (se existir um `UserCache` no contexto).
     */
    @Bean
    AuthenticationProvider authenticationProvider(
            UserDetailsService uds,
            PasswordEncoder encoder,
            ObjectProvider<UserCache> userCacheOpt
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        UserCache userCache = userCacheOpt.getIfAvailable();
        if (userCache != null) provider.setUserCache(userCache);
        return provider;
    }

    /*
     * # 🚧 (Opcional) AccessDeniedHandler custom
     * - **Uso alternativo** ao forward padrão para `/acesso-negado`.
     * - Redireciona de volta para a página anterior com `?denied=1` e grava uma flash na sessão.
     * - **Desabilitado por padrão** (veja bloco comentado em `.exceptionHandling`).
     */
    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            request.getSession().setAttribute("FLASH_MSG_ERRO", "Você não tem permissão para realizar esta ação.");
            String referer  = request.getHeader("Referer");
            String fallback = request.getContextPath() + "/agendamentos/ui";
            String target   = (referer != null && !referer.isBlank()) ? referer : fallback;
            String sep      = target.contains("?") ? "&" : "?";
            response.sendRedirect(target + sep + "denied=1"); // PRG
        };
    }

    /*
     * # 🌐 HTTP Security (SecurityFilterChain)
     * - **CSRF**: ativo para a UI; ignorado nas **APIs** (`/api/**`).
     * - **Autorização por URL**: UI consistente sem exigir `@PreAuthorize` (se preferir).
     * - **Acesso negado (403)**: **forward** para `/acesso-negado` (view Thymeleaf) — simples e padronizado.
     * - **Login/Logout**: formulário tradicional, pós-login sempre em `/home/ui`.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            AuthenticationProvider authProvider) throws Exception {
        http
                /* ## CSRF
                 * - Mantenha habilitado para páginas Thymeleaf.
                 * - Ignore somente caminhos de **API**. */
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

                /* ## Autorização por URL
                 * - Ajuste conforme seus endpoints reais. */
                .authorizeHttpRequests(auth -> auth
                        // Públicos
                        .requestMatchers(
                                "/", "/index",
                                "/login", "/error", "/error/403",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico"
                        ).permitAll()

                        // Home autenticada (dashboard)
                        .requestMatchers("/home/ui").hasAnyRole("USER","ADMIN")

                        // UI (exemplos)
                        .requestMatchers("/usuarios/ui/**").authenticated()
                        .requestMatchers("/agendamentos/ui/**").hasAnyRole("USER","ADMIN")

                        // APIs REST
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/usuarios/**").authenticated()

                        // Área admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Demais rotas
                        .anyRequest().authenticated()
                )

                /* ## Request Cache
                 * - Não reaproveitar a última requisição; pós-login vai para a URL definida. */
                .requestCache(c -> c.requestCache(new NullRequestCache()))

                /* ## Autenticação */
                .authenticationProvider(authProvider)

                /* ## Login (form) */
                .formLogin(form -> form
                        .loginPage("/login")                 // GET /login -> templates/login.html
                        .loginProcessingUrl("/login")        // POST /login
                        .defaultSuccessUrl("/home/ui", true) // sempre vai para a home
                        .failureUrl("/login?error")
                        .permitAll()
                )

                /* ## Logout */
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                /* ## Access Denied (403)
                 * ### Opção A — Simples (ativada):
                 * - Forward para `/acesso-negado` (renderiza `templates/erro/acesso-negado.html`). */
                .exceptionHandling(e -> e.accessDeniedPage("/acesso-negado"));


        return http.build();
    }
}
