package br.com.fiap.mototrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * # 🔐 SecurityConfiguration (MotoTrack — Sprint 3, JPA)
 *
 * ## ✅ O que esta classe entrega
 * - **Autenticação via formulário** (login/logout)
 * - **Perfis de acesso**: `OPERADOR`, `GESTOR`, `ADMINISTRADOR`
 * - **Proteção de rotas** por perfil (URL-based)
 * - **CSRF habilitado** (compatível com forms Thymeleaf)
 *
 * > Observação:
 * > - Os usuários são carregados do **banco** via `UserDetailsService` (implementação JPA separada).
 * > - Garanta que sua implementação (`JpaUserDetailsService`) mapeie `perfil` → `roles(...)`.
 */
@Configuration
@EnableMethodSecurity // opcional: habilita @PreAuthorize nos métodos (defesa fina)
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth

                        // --- 🌐 Público (assets + tela de login) ---
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/login", "/logout").permitAll()

                        // --- 👤 Usuários ---
                        // ADMINISTRADOR gerencia o módulo de usuários (CRUD completo)
                        .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")

                        // --- 📅 Agendamentos ---
                        // OPERADOR, GESTOR e ADMINISTRADOR podem acessar/operar agendamentos
                        .requestMatchers("/agendamentos/**").hasAnyRole("OPERADOR", "GESTOR", "ADMINISTRADOR")

                        // --- 🔒 Demais rotas exigem autenticação ---
                        .anyRequest().authenticated()
                )

                // ✅ 🔑 Login por formulário
                .formLogin(login -> login
                        .loginPage("/login")                 // sua página Thymeleaf (templates/login.html)
                        .defaultSuccessUrl("/agendamentos", true) // destino pós-login
                        .permitAll()
                )

                // ✅ 🚪 Logout (sempre via POST /logout)
                .logout(l -> l
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")  // feedback na tela de login
                        .permitAll()
                );

        // CSRF permanece habilitado por padrão → incluir token nos forms
        return http.build();
    }

    /**
     * ## 🔒 PasswordEncoder
     * Use **BCrypt** para gravar/validar senhas.
     * > Lembrete: no `UsuarioService`, ao **cadastrar/atualizar**, codifique a senha:
     * > `usuario.setSenha(passwordEncoder.encode(dto.getSenha()));`
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
