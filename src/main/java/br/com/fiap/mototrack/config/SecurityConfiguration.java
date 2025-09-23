package br.com.fiap.mototrack.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // habilita @PreAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService; // seu bean JPA já existente
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            AuthenticationProvider authProvider) throws Exception {
        http
                // CSRF habilitado (bom para Thymeleaf/forms)
                .csrf(csrf -> { })

                .authorizeHttpRequests(auth -> auth
                        // 🔓 Swagger + estáticos
                        .requestMatchers(
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/css/**", "/js/**", "/images/**", "/webjars/**"
                        ).permitAll()

                        // 🔓 Login e erro
                        .requestMatchers("/login", "/error").permitAll()

                        // 🌐 UI Thymeleaf (interface)
                        .requestMatchers("/usuarios/ui/**").authenticated()

                        // 🌐 API REST (JSON)
                        .requestMatchers("/usuarios/**").authenticated()

                        // (se tiver um painel admin separado, mantenha também)
                        .requestMatchers("/admin/**").authenticated()

                        // 🔒 qualquer outra rota requer autenticação
                        .anyRequest().authenticated()
                )

                // Autenticação: seu UserDetailsService + BCrypt
                .authenticationProvider(authProvider)

                // Form login
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/usuarios/ui", true) // UI como landing pós-login
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

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // JPA
        provider.setPasswordEncoder(encoder);
        return provider;
    }
}
