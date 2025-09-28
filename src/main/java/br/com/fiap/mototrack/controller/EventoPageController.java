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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UI (Thymeleaf) - EventoPageController
 *
 * Renderiza páginas HTML (lista + form) para gestão de eventos.
 * Rotas: /eventos/ui/**
 *
 * Segurança:
 * - USER/ADMIN: leitura (lista, detalhes se houver).
 * - ADMIN: criação/edição/exclusão.
 *
 * Views esperadas:
 * - templates/eventos/list.html
 * - templates/eventos/form.html
 */
@Controller
@RequestMapping("/eventos/ui")
@RequiredArgsConstructor
public class EventoPageController {

    private static final Logger log = LoggerFactory.getLogger(EventoPageController.class);

    private final EventoService service;
    private final ModelMapper modelMapper;

    /** Binder para campos <input type="datetime-local"> (HTML5). */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override public void setAsText(String text) {
                setValue((text == null || text.isBlank()) ? null : LocalDateTime.parse(text, fmt));
            }
            @Override public String getAsText() {
                LocalDateTime v = (LocalDateTime) getValue();
                return v == null ? "" : v.format(fmt);
            }
        });
    }

    /**
     * GET /eventos/ui
     * Lista paginada + filtro para a view.
     * Ordenação padrão: dataHora DESC (ajuste se seu DTO usar outro nome).
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public String listar(
            @ParameterObject EventoFilter filtro,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable,
            Model model,
            @RequestParam(value = "denied", required = false) String denied,
            jakarta.servlet.http.HttpServletRequest req
    ) {
        // 1) Mensagem vinda do AccessDeniedHandler (?denied=1)
        if ("1".equals(denied)) {
            model.addAttribute("msgErro", "Você não tem permissão para realizar esta ação.");
        }

        // 2) Mensagens 'flash' guardadas em sessão (erro/ok)
        jakarta.servlet.http.HttpSession session = req.getSession(false);
        if (session != null) {
            Object fe = session.getAttribute("FLASH_MSG_ERRO");
            if (fe != null) {
                model.addAttribute("msgErro", fe.toString());
                session.removeAttribute("FLASH_MSG_ERRO");
            }
            Object fk = session.getAttribute("FLASH_MSG_OK");
            if (fk != null) {
                model.addAttribute("msgSucesso", fk.toString());
                session.removeAttribute("FLASH_MSG_OK");
            }
        }

        log.info("UI >> listando eventos | filtro={}, pageable={}", filtro, pageable);
        Page<EventoResponse> page = service.consultarComFiltro(filtro, pageable);
        model.addAttribute("page", page);
        model.addAttribute("filtro", filtro);
        return "eventos/list"; // templates/eventos/list.html
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
        // Ex.: combos auxiliares (motos, filiais, etc.) podem ser adicionadas aqui
        return "eventos/form"; // templates/eventos/form.html
    }

    /**
     * POST /eventos/ui
     * Processa criação via form (com CSRF no template).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String criar(@ModelAttribute("evento") @Valid EventoRequest evento,
                        BindingResult binding,
                        RedirectAttributes ra,
                        Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("id", null); // mantém o título "Novo Evento"
            return "eventos/form";
        }
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
                            RedirectAttributes ra,
                            Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("id", id);
            return "eventos/form";
        }
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
        try {
            service.excluir(id);
            ra.addFlashAttribute("msgSucesso", "Evento excluído com sucesso.");
        } catch (ResponseStatusException e) {
            ra.addFlashAttribute("msgErro", e.getReason() != null ? e.getReason() : "Não foi possível excluir.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("msgErro", "Não foi possível excluir: registro em uso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("msgErro", "Erro inesperado ao excluir. Tente novamente.");
        }
        return "redirect:/eventos/ui";
    }
}
