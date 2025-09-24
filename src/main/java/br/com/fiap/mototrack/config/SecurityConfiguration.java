package br.com.fiap.mototrack.config;

import br.com.fiap.mototrack.model.Usuario;
import br.com.fiap.mototrack.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // habilita @PreAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserCache;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    // === Beans base ==========================================================

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Carrega usuários do banco por e-mail (case-insensitive).
     * Converte perfis do domínio para authorities Spring:
     *  - ADMINISTRADOR/GESTOR -> ROLE_ADMIN
     *  - OPERADOR -> ROLE_USER
     *  - USER/ADMIN mantidos
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

    /**
     * Provider de autenticação usando JPA + BCrypt.
     * Se existir um bean UserCache (ex.: do CacheConfig com Caffeine),
     * ele é injetado e usado para reduzir consultas repetidas no login.
     */
    @Bean
    AuthenticationProvider authenticationProvider(
            UserDetailsService uds,
            PasswordEncoder encoder,
            // opcional: só injeta se você tiver um bean UserCache configurado
            org.springframework.beans.factory.ObjectProvider<UserCache> userCacheOpt
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        UserCache userCache = userCacheOpt.getIfAvailable();
        if (userCache != null) {
            provider.setUserCache(userCache);
        }
        return provider;
    }

    // === HTTP security =======================================================

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            AuthenticationProvider authProvider) throws Exception {
        http
                // CSRF habilitado; ignora na API JSON
                .csrf(csrf -> csrf.ignoringRequestMatchers("/usuarios/**"))

                .authorizeHttpRequests(auth -> auth
                        // 🔓 Swagger + estáticos
                        .requestMatchers(
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // 🔓 Login e erro
                        .requestMatchers("/login", "/error").permitAll()

                        // 🌐 UI Thymeleaf (interface)
                        .requestMatchers("/usuarios/ui/**").authenticated()

                        // 🌐 API REST (JSON)
                        .requestMatchers("/usuarios/**").authenticated()

                        // (exemplo) área admin
                        .requestMatchers("/admin/**").authenticated()

                        // 🔒 o restante exige autenticação
                        .anyRequest().authenticated()
                )

                // Autenticação: seu UserDetailsService + BCrypt (+ cache se existir)
                .authenticationProvider(authProvider)

                // Form login
                .formLogin(form -> form
                        .loginPage("/login")                      // precisa existir /templates/login.html
                        .defaultSuccessUrl("/usuarios/ui", true)  // landing pós-login
                        .permitAll()
                )

                // Logout (POST /logout) com CSRF
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
