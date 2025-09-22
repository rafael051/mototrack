package br.com.fiap.mototrack.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ## 🔐 Controller: AuthController (MotoTrack)
 *
 * Responsável pelas páginas de autenticação (login/logout) usadas pelo Spring Security.
 * Observações:
 * - O Spring Security executa o **logout via POST em /logout** (não por GET).
 * - Este controller apenas **renderiza** as telas `login.html` e `logout.html`.
 */
@Controller
public class AuthController {

    /**
     * ### 🔑 GET /login
     * Exibe a página de **login por formulário**.
     * Mostra mensagens quando `?error` (credenciais inválidas) ou `?logout` (sessão encerrada).
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ){
        if (error != null)  model.addAttribute("error", "Credenciais inválidas.");
        if (logout != null) model.addAttribute("logout", "Sessão encerrada com sucesso.");
        return "login"; // templates/login.html
    }

    /**
     * ### 🚪 GET /logout
     * Exibe uma **tela de confirmação** com um formulário que envia **POST /logout**.
     * Quem encerra a sessão é o Spring Security.
     */
    @GetMapping("/logout")
    public String logoutPage(){
        return "logout"; // templates/logout.html
    }
}
