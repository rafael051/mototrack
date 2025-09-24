package br.com.fiap.mototrack.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Controller
public class HomePageController {

    // ⛔️ NÃO mapeia "/" (evita conflito com HomeController)
    // ⛔️ NÃO mapeia "/login" (evita conflito com AuthController)

    /**
     * Dashboard autenticada (usa a mesma view "index").
     * Requer ROLE_USER ou ROLE_ADMIN.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/home/ui")
    public String homeUi(Authentication auth, Model model) {
        // Sinaliza ao template que está autenticado e injeta infos úteis
        model.addAttribute("autenticado", true);
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("authorities", auth.getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        }
        return "index"; // src/main/resources/templates/index.html
    }
}
