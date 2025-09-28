package br.com.fiap.mototrack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * # MvcViewConfig
 *
 * ## Finalidade
 * Registrar mapeamentos simples de URL → View (sem Controller) para telas que
 * não precisam de lógica. Mantém a UI padronizada.
 *
 * ## Integração com Segurança
 * Usado em conjunto com:
 *   .exceptionHandling(e -> e.accessDeniedPage("/acesso-negado"))
 * Quando ocorrer 403, o Spring fará **forward** para `/acesso-negado`.
 *
 * ## Convenções
 * - Views Thymeleaf em: src/main/resources/templates/
 * - Ajuste o `setViewName` se o prefix/suffix do Thymeleaf tiver sido alterado.
 */
@Configuration
public class MvcViewConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 403 (Acesso negado) — view global para toda a UI
        registry.addViewController("/acesso-negado")
                .setViewName("erro/acesso-negado"); // templates/erro/acesso-negado.html
    }
}
