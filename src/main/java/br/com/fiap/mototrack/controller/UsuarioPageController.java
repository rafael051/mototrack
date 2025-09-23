package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.UsuarioRequest;
import br.com.fiap.mototrack.dto.response.UsuarioResponse;
import br.com.fiap.mototrack.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * ## 👤 Controller MVC: UsuarioPageController (Thymeleaf)
 *
 * Rotas de **interface (UI)** para gestão de usuários via páginas HTML.
 * Mantém separado da API REST (`/usuarios` → JSON) para não conflitar conteúdo/headers.
 *
 * ### Rotas (UI)
 * - Lista:     GET  /usuarios/ui
 * - Novo:      GET  /usuarios/ui/novo
 * - Criar:     POST /usuarios/ui
 * - Editar:    GET  /usuarios/ui/{id}/editar
 * - Atualizar: POST /usuarios/ui/{id}
 * - Excluir:   POST /usuarios/ui/{id}/excluir
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/usuarios/ui")
public class UsuarioPageController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioPageController.class);
    private final UsuarioService service;
    private final ModelMapper modelMapper;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public String listar(Model model) {
        log.info("UI: Listando usuários (página)");
        List<UsuarioResponse> usuarios = service.consultarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/lista";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        log.info("UI: Novo usuário (form)");
        model.addAttribute("usuario", new UsuarioRequest());
        model.addAttribute("modo", "CRIAR");
        return "usuarios/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String criar(@ModelAttribute("usuario") @Valid UsuarioRequest usuario,
                        BindingResult binding,
                        RedirectAttributes ra) {
        if (binding.hasErrors()) return "usuarios/form";
        service.cadastrar(usuario);
        ra.addFlashAttribute("msgSucesso", "Usuário criado com sucesso.");
        return "redirect:/usuarios/ui";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        log.info("UI: Editar usuário ID: {}", id);
        var existente = service.buscarPorId(id);
        var req = modelMapper.map(existente, UsuarioRequest.class);
        model.addAttribute("usuario", req);
        model.addAttribute("id", id);
        model.addAttribute("modo", "EDITAR");
        return "usuarios/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute("usuario") @Valid UsuarioRequest usuario,
                            BindingResult binding,
                            RedirectAttributes ra) {
        if (binding.hasErrors()) return "usuarios/form";
        service.atualizar(id, usuario);
        ra.addFlashAttribute("msgSucesso", "Usuário atualizado com sucesso.");
        return "redirect:/usuarios/ui";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        service.excluir(id);
        ra.addFlashAttribute("msgSucesso", "Usuário excluído com sucesso.");
        return "redirect:/usuarios/ui";
    }
}
