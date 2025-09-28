package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.MotoRequest;
import br.com.fiap.mototrack.dto.response.MotoResponse;
import br.com.fiap.mototrack.filter.MotoFilter;
import br.com.fiap.mototrack.service.MotoService;
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

@Controller
@RequestMapping("/motos/ui")
@RequiredArgsConstructor
public class MotoPageController {

    private static final Logger log = LoggerFactory.getLogger(MotoPageController.class);

    private final MotoService service;
    private final ModelMapper modelMapper;

    /**
     * GET /motos/ui
     * Lista paginada + filtro (ex.: placa, marca, modelo, status).
     * Ordenação padrão: placa ASC.
     * Aceita ?denied=1 vindo do AccessDeniedHandler.
     * Lê mensagens flash salvas em sessão (FLASH_MSG_OK / FLASH_MSG_ERRO).
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public String listar(
            @ParameterObject MotoFilter filtro,
            @PageableDefault(size = 10, sort = "placa", direction = Sort.Direction.ASC) Pageable pageable,
            Model model,
            @RequestParam(value = "denied", required = false) String denied,
            HttpServletRequest req
    ) {
        if ("1".equals(denied)) {
            model.addAttribute("msgErro", "Você não tem permissão para realizar esta ação.");
        }

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

        log.info("UI >> listando motos | filtro={}, pageable={}", filtro, pageable);
        Page<MotoResponse> page = service.consultarComFiltro(filtro, pageable);
        model.addAttribute("page", page);
        model.addAttribute("filtro", filtro);
        return "motos/list"; // templates/motos/list.html
    }

    /**
     * GET /motos/ui/novo
     * Exibe o formulário de criação.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("moto", new MotoRequest());
        model.addAttribute("id", null);
        // Ex.: se houver combo de filiais: model.addAttribute("filiais", filialService.consultarTodos());
        return "motos/form"; // templates/motos/form.html
    }

    /**
     * POST /motos/ui
     * Cria a moto via form.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String criar(@ModelAttribute("moto") @Valid MotoRequest moto,
                        BindingResult binding,
                        RedirectAttributes ra,
                        Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("id", null);
            return "motos/form";
        }
        service.cadastrar(moto);
        ra.addFlashAttribute("msgSucesso", "Moto cadastrada com sucesso.");
        return "redirect:/motos/ui";
    }

    /**
     * GET /motos/ui/{id}/editar
     * Exibe o formulário de edição com dados preenchidos.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        MotoResponse existente = service.buscarPorId(id);
        MotoRequest req = modelMapper.map(existente, MotoRequest.class);
        model.addAttribute("moto", req);
        model.addAttribute("id", id);
        // Se usar combo de filial, injete novamente a lista aqui.
        return "motos/form";
    }

    /**
     * POST /motos/ui/{id}
     * Atualiza a moto via form.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute("moto") @Valid MotoRequest moto,
                            BindingResult binding,
                            RedirectAttributes ra,
                            Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("id", id);
            return "motos/form";
        }
        service.atualizar(id, moto);
        ra.addFlashAttribute("msgSucesso", "Moto atualizada com sucesso.");
        return "redirect:/motos/ui";
    }

    /**
     * POST /motos/ui/{id}/excluir
     * Exclui a moto e retorna para a lista (com tratamento de erros).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("msgSucesso", "Moto excluída com sucesso.");
        } catch (ResponseStatusException e) {
            ra.addFlashAttribute("msgErro", e.getReason() != null ? e.getReason() : "Não foi possível excluir.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("msgErro", "Não foi possível excluir: registro em uso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("msgErro", "Erro inesperado ao excluir. Tente novamente.");
        }
        return "redirect:/motos/ui";
    }
}
