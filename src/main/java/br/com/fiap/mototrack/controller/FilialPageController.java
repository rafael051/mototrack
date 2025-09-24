package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.FilialRequest;
import br.com.fiap.mototrack.dto.response.FilialResponse;
import br.com.fiap.mototrack.filter.FilialFilter;
import br.com.fiap.mototrack.service.FilialService;
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
 * UI (Thymeleaf) - FilialPageController
 *
 * Renderiza páginas HTML (lista + form) para gestão de filiais.
 * Rotas: /filiais/ui/** → retorna nomes de templates.
 *
 * Segurança:
 * - USER/ADMIN: leitura (lista).
 * - ADMIN: criação/edição/exclusão.
 *
 * Views esperadas:
 * - templates/filiais/lista.html
 * - templates/filiais/form.html
 */
@Controller
@RequestMapping("/filiais/ui")
@RequiredArgsConstructor
public class FilialPageController {

    private static final Logger log = LoggerFactory.getLogger(FilialPageController.class);

    private final FilialService service;
    private final ModelMapper modelMapper;

    /**
     * GET /filiais/ui
     * Lista paginada + filtro para a view.
     * Ordenação padrão: nome ASC.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public String listar(
            @ParameterObject FilialFilter filtro,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        log.info("UI >> listando filiais | filtro={}, pageable={}", filtro, pageable);
        Page<FilialResponse> page = service.consultarComFiltro(filtro, pageable);
        model.addAttribute("page", page);
        model.addAttribute("filtro", filtro);
        return "filiais/lista";
    }

    /**
     * GET /filiais/ui/novo
     * Exibe formulário de criação.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("filial", new FilialRequest());
        model.addAttribute("id", null);
        return "filiais/form";
    }

    /**
     * POST /filiais/ui
     * Processa criação via form (com CSRF no template).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String criar(@ModelAttribute("filial") @Valid FilialRequest filial,
                        BindingResult binding,
                        RedirectAttributes ra) {
        if (binding.hasErrors()) {
            return "filiais/form";
        }
        service.cadastrar(filial);
        ra.addFlashAttribute("msgSucesso", "Filial criada com sucesso.");
        return "redirect:/filiais/ui";
    }

    /**
     * GET /filiais/ui/{id}/editar
     * Exibe formulário de edição com dados preenchidos.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        FilialResponse existente = service.buscarPorId(id);
        FilialRequest req = modelMapper.map(existente, FilialRequest.class);
        model.addAttribute("filial", req);
        model.addAttribute("id", id);
        return "filiais/form";
    }

    /**
     * POST /filiais/ui/{id}
     * Processa atualização via form.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute("filial") @Valid FilialRequest filial,
                            BindingResult binding,
                            RedirectAttributes ra) {
        if (binding.hasErrors()) {
            return "filiais/form";
        }
        service.atualizar(id, filial);
        ra.addFlashAttribute("msgSucesso", "Filial atualizada com sucesso.");
        return "redirect:/filiais/ui";
    }

    /**
     * POST /filiais/ui/{id}/excluir
     * Exclui e retorna para a lista.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        service.excluir(id);
        ra.addFlashAttribute("msgSucesso", "Filial excluída com sucesso.");
        return "redirect:/filiais/ui";
    }
}
