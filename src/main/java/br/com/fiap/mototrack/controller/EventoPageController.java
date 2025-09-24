package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.EventoRequest;
import br.com.fiap.mototrack.dto.response.EventoResponse;
import br.com.fiap.mototrack.filter.EventoFilter;
import br.com.fiap.mototrack.service.EventoService;
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
 * UI (Thymeleaf) - EventoPageController
 *
 * Renderiza páginas HTML (lista + form) para gestão de eventos.
 * Rotas: /eventos/ui/*
 *
 * Segurança:
 * - USER/ADMIN: listar/visualizar.
 * - ADMIN: criar/editar/excluir.
 *
 * Views esperadas:
 * - templates/eventos/lista.html
 * - templates/eventos/form.html
 */
@Controller
@RequestMapping("/eventos/ui")
@RequiredArgsConstructor
public class EventoPageController {

    private static final Logger log = LoggerFactory.getLogger(EventoPageController.class);

    private final EventoService service;
    private final ModelMapper modelMapper;

    /**
     * GET /eventos/ui
     * Lista paginada + filtro.
     * Campo padrão de ordenação: dataHora (ajuste se o seu DTO usar outro nome).
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public String listar(
            @ParameterObject EventoFilter filtro,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        log.info("UI >> listando eventos | filtro={}, pageable={}", filtro, pageable);
        Page<EventoResponse> page = service.consultarComFiltro(filtro, pageable);
        model.addAttribute("page", page);
        model.addAttribute("filtro", filtro);
        return "eventos/lista"; // templates/eventos/lista.html
    }

    /**
     * GET /eventos/ui/novo
     * Exibe formulário de criação.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("evento", new EventoRequest());
        model.addAttribute("id", null);
        // Se precisar popular combos (ex.: motos), injete aqui:
        // model.addAttribute("motos", motoService.listarAtivas());
        return "eventos/form"; // templates/eventos/form.html
    }

    /**
     * POST /eventos/ui
     * Processa criação via form.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String criar(@ModelAttribute("evento") @Valid EventoRequest evento,
                        BindingResult binding,
                        RedirectAttributes ra) {
        if (binding.hasErrors()) return "eventos/form";
        service.cadastrar(evento);
        ra.addFlashAttribute("msgSucesso", "Evento criado com sucesso.");
        return "redirect:/eventos/ui";
    }

    /**
     * GET /eventos/ui/{id}/editar
     * Exibe formulário de edição com dados preenchidos.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        EventoResponse existente = service.buscarPorId(id);
        EventoRequest req = modelMapper.map(existente, EventoRequest.class);
        model.addAttribute("evento", req);
        model.addAttribute("id", id);
        // Se precisar de listas auxiliares, injete novamente aqui.
        return "eventos/form";
    }

    /**
     * POST /eventos/ui/{id}
     * Processa atualização via form.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute("evento") @Valid EventoRequest evento,
                            BindingResult binding,
                            RedirectAttributes ra) {
        if (binding.hasErrors()) return "eventos/form";
        service.atualizar(id, evento);
        ra.addFlashAttribute("msgSucesso", "Evento atualizado com sucesso.");
        return "redirect:/eventos/ui";
    }

    /**
     * POST /eventos/ui/{id}/excluir
     * Exclui e retorna para a lista.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        service.excluir(id);
        ra.addFlashAttribute("msgSucesso", "Evento excluído com sucesso.");
        return "redirect:/eventos/ui";
    }
}
