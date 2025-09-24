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
import org.springframework.security.web.savedrequest.NullRequestCache;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    // === Criptografia ========================================================
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // === UserDetailsService (normaliza perfis) ===============================
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

    // === Provider com cache opcional ========================================
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

    // === HTTP Security =======================================================
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            AuthenticationProvider authProvider) throws Exception {
        http
                // CSRF: habilitado na UI; ignorado na API JSON
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/usuarios/**" // inclua outras APIs REST aqui, se houver
                ))

                // Autorização
                .authorizeHttpRequests(auth -> auth
                        // Públicos
                        .requestMatchers(
                                "/", "/index",
                                "/login", "/error",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico"
                        ).permitAll()

                        // Home autenticada (dashboard)
                        .requestMatchers("/home/ui").hasAnyRole("USER","ADMIN")

                        // UI protegida
                        .requestMatchers("/usuarios/ui/**").authenticated()

                        // API protegida
                        .requestMatchers("/usuarios/**").authenticated()

                        // Área admin (exemplo)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Demais rotas
                        .anyRequest().authenticated()
                )

                // Ignora SavedRequest: pós-login sempre vai para o destino definido
                .requestCache(c -> c.requestCache(new NullRequestCache()))

                // Provider custom
                .authenticationProvider(authProvider)

                // Login form
                .formLogin(form -> form
                        .loginPage("/login")              // GET /login -> templates/login.html
                        .loginProcessingUrl("/login")     // POST /login
                        .defaultSuccessUrl("/home/ui", true)     // ✅ SEMPRE voltar para a home
                        .failureUrl("/login?error")
                        .permitAll()
                )

                // Logout (POST /logout) com CSRF
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // (Opcional) página de acesso negado se quiser customizar
                .exceptionHandling(ex -> ex.accessDeniedPage("/error/403"));

        return http.build();
    }
}
