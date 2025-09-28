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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
 * - templates/filiais/list.html
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
     * Também trata ?denied=1 e mensagens flash vindas da sessão.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public String listar(
            @ParameterObject FilialFilter filtro,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable,
            Model model,
            @RequestParam(value = "denied", required = false) String denied,
            HttpServletRequest req
    ) {
        // 1) Mensagem de Acesso Negado via query param (?denied=1)
        if ("1".equals(denied)) {
            model.addAttribute("msgErro", "Você não tem permissão para realizar esta ação.");
        }

        // 2) Mensagens flash guardadas em sessão por outros handlers
        HttpSession session = req.getSession(false);
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

        log.info("UI >> listando filiais | filtro={}, pageable={}", filtro, pageable);

        Page<FilialResponse> page = service.consultarComFiltro(filtro, pageable);
        model.addAttribute("page", page);
        model.addAttribute("filtro", filtro);
        return "filiais/list";
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
                        RedirectAttributes ra,
                        Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("id", null);
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
                            RedirectAttributes ra,
                            Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("id", id);
            return "filiais/form";
        }
        service.atualizar(id, filial);
        ra.addFlashAttribute("msgSucesso", "Filial atualizada com sucesso.");
        return "redirect:/filiais/ui";
    }

    /**
     * POST /filiais/ui/{id}/excluir
     * Exclui e retorna para a lista.
     * Trata exceções comuns com mensagens amigáveis.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("msgSucesso", "Filial excluída com sucesso.");
        } catch (ResponseStatusException e) {
            ra.addFlashAttribute("msgErro", e.getReason() != null ? e.getReason() : "Não foi possível excluir.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("msgErro", "Não foi possível excluir: registro em uso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("msgErro", "Erro inesperado ao excluir. Tente novamente.");
        }
        return "redirect:/filiais/ui";
    }
}
