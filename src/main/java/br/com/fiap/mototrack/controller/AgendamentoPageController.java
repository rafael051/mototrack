package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.AgendamentoRequest;
import br.com.fiap.mototrack.dto.response.AgendamentoResponse;
import br.com.fiap.mototrack.filter.AgendamentoFilter;
import br.com.fiap.mototrack.service.AgendamentoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UI (Thymeleaf) - AgendamentoPageController
 *
 * Responsável por renderizar páginas HTML (lista + form) para gestão de agendamentos.
 * Rotas iniciam com /agendamentos/ui e retornam nomes de templates.
 *
 * Segurança:
 * - USER/ADMIN: leitura (lista, detalhes se houver).
 * - ADMIN: criação/edição/exclusão.
 *
 * Views esperadas:
 * - templates/agendamentos/lista.html
 * - templates/agendamentos/form.html
 */
@Controller
@RequestMapping("/agendamentos/ui")
@RequiredArgsConstructor
public class AgendamentoPageController {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoPageController.class);

    private final AgendamentoService service;
    private final ModelMapper modelMapper;

    /** Binder para datetime-local (HTML5). */
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
     * GET /agendamentos/ui
     * Lista paginada + filtro para a view.
     * Filtros típicos em AgendamentoFilter: dataInicial, dataFinal, descricao, idMoto, etc.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public String listar(
            @ParameterObject AgendamentoFilter filtro,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            Model model,
            @RequestParam(value = "denied", required = false) String denied,      // ← aceita ?denied=1
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

        log.info("UI >> listando agendamentos | filtro={}, pageable={}", filtro, pageable);

        Page<AgendamentoResponse> page = service.consultarComFiltro(filtro, pageable);
        model.addAttribute("page", page);
        model.addAttribute("filtro", filtro);
        return "agendamentos/list";
    }


    /**
     * GET /agendamentos/ui/novo
     * Exibe formulário de criação.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("agendamento", new AgendamentoRequest());
        model.addAttribute("id", null);
        return "agendamentos/form"; // templates/agendamentos/form.html
    }

    /**
     * POST /agendamentos/ui
     * Processa criação via form (com CSRF no template).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String criar(@ModelAttribute("agendamento") @Valid AgendamentoRequest agendamento,
                        BindingResult binding,
                        RedirectAttributes ra,
                        Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("id", null); // <-- mantém o título "Novo Agendamento"
            return "agendamentos/form";
        }
        service.cadastrar(agendamento);
        ra.addFlashAttribute("msgSucesso", "Agendamento criado com sucesso.");
        return "redirect:/agendamentos/ui";
    }

    /**
     * GET /agendamentos/ui/{id}/editar
     * Exibe formulário de edição com dados preenchidos.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        AgendamentoResponse existente = service.buscarPorId(id);
        // Reaproveita o mesmo form mapeando Response -> Request
        AgendamentoRequest req = modelMapper.map(existente, AgendamentoRequest.class);
        model.addAttribute("agendamento", req);
        model.addAttribute("id", id);
        return "agendamentos/form";
    }

    /**
     * POST /agendamentos/ui/{id}
     * Processa atualização via form.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute("agendamento") @Valid AgendamentoRequest agendamento,
                            BindingResult binding,
                            RedirectAttributes ra,
                            Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("id", id);
            return "agendamentos/form";
        }
        service.atualizar(id, agendamento);
        ra.addFlashAttribute("msgSucesso", "Agendamento atualizado com sucesso.");
        return "redirect:/agendamentos/ui";
    }

    /**
     * POST /agendamentos/ui/{id}/excluir
     * Exclui e retorna para a lista.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("msgSucesso", "Agendamento excluído com sucesso.");
        } catch (org.springframework.web.server.ResponseStatusException e) {
            // Ex.: 404 ou 409 lançados pelo service
            ra.addFlashAttribute("msgErro", e.getReason() != null ? e.getReason() : "Não foi possível excluir.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Ex.: registro em uso/constraint
            ra.addFlashAttribute("msgErro", "Não foi possível excluir: registro em uso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("msgErro", "Erro inesperado ao excluir. Tente novamente.");
        }
        return "redirect:/agendamentos/ui";
    }
}
