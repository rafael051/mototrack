package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.UsuarioRequest;
import br.com.fiap.mototrack.dto.response.UsuarioResponse;
import br.com.fiap.mototrack.filter.UsuarioFilter;
import br.com.fiap.mototrack.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * UI (Thymeleaf) - UsuarioPageController
 *
 * Responsável por renderizar páginas HTML (lista + form) para gestão de usuários.
 * Rotas iniciam com /usuarios/ui e retornam nomes de templates.
 *
 * Segurança:
 * - USER/ADMIN: leitura (lista).
 * - ADMIN: criação/edição/exclusão.
 */
@Controller
@RequestMapping("/usuarios/ui")
@RequiredArgsConstructor
public class UsuarioPageController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioPageController.class);

    private final UsuarioService service;
    private final ModelMapper modelMapper;

    /**
     * GET /usuarios/ui
     * Lista paginada + filtro (nome, email) para a view.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public String listar(
            @ParameterObject UsuarioFilter filtro,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        log.info("UI >> listando usuários | filtro={}, pageable={}", filtro, pageable);
        Page<UsuarioResponse> page = service.consultarComFiltro(filtro, pageable);
        model.addAttribute("page", page);
        model.addAttribute("filtro", filtro);
        return "usuarios/list"; // templates/usuarios/list.html
    }

    /**
     * GET /usuarios/ui/novo
     * Exibe form de criação.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new UsuarioRequest());
        model.addAttribute("id", null);
        return "usuarios/form"; // templates/usuarios/form.html
    }

    /**
     * POST /usuarios/ui
     * Processa criação via form.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String criar(@ModelAttribute("usuario") @Valid UsuarioRequest usuario,
                        BindingResult binding,
                        RedirectAttributes ra,
                        Model model) { // <- adicionado para manter coerência com o template
        if (binding.hasErrors()) {
            // Mantém a semântica do form que usa o atributo 'id' para decidir criação/edição
            model.addAttribute("id", null);
            return "usuarios/form";
        }
        service.cadastrar(usuario);
        ra.addFlashAttribute("msgSucesso", "Usuário criado com sucesso.");
        return "redirect:/usuarios/ui";
    }

    /**
     * GET /usuarios/ui/{id}/editar
     * Exibe form de edição com dados preenchidos.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        UsuarioResponse existente = service.buscarPorId(id);
        UsuarioRequest req = modelMapper.map(existente, UsuarioRequest.class);
        model.addAttribute("usuario", req);
        model.addAttribute("id", id);
        return "usuarios/form";
    }

    /**
     * POST /usuarios/ui/{id}
     * Processa atualização via form.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute("usuario") @Valid UsuarioRequest usuario,
                            BindingResult binding,
                            RedirectAttributes ra,
                            Model model) { // <- adicionado para repassar 'id' ao form em caso de erro
        if (binding.hasErrors()) {
            // Reenvia o 'id' para o template poder montar a action correta (/usuarios/ui/{id})
            model.addAttribute("id", id);
            return "usuarios/form";
        }
        service.atualizar(id, usuario);
        ra.addFlashAttribute("msgSucesso", "Usuário atualizado com sucesso.");
        return "redirect:/usuarios/ui";
    }

    /**
     * POST /usuarios/ui/{id}/excluir
     * Exclui e retorna para a lista.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        service.excluir(id);
        ra.addFlashAttribute("msgSucesso", "Usuário excluído com sucesso.");
        return "redirect:/usuarios/ui";
    }
}
