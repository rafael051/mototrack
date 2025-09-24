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
 * UI (Thymeleaf) - MotoPageController
 *
 * Renderiza páginas HTML (lista + form) para gestão de motocicletas.
 * Rotas: /motos/ui/** → retorna nomes de templates.
 *
 * Segurança:
 * - USER/ADMIN: leitura (lista).
 * - ADMIN: criação/edição/exclusão.
 *
 * Views esperadas:
 * - templates/motos/lista.html
 * - templates/motos/form.html
 */
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
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public String listar(
            @ParameterObject MotoFilter filtro,
            @PageableDefault(size = 10, sort = "placa", direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        log.info("UI >> listando motos | filtro={}, pageable={}", filtro, pageable);
        Page<MotoResponse> page = service.consultarComFiltro(filtro, pageable);
        model.addAttribute("page", page);
        model.addAttribute("filtro", filtro);
        return "motos/lista"; // templates/motos/lista.html
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
        // Se o form tiver seleção de filial, injete a lista aqui (ex. model.addAttribute("filiais", filialService.consultarTodos()))
        return "motos/form"; // templates/motos/form.html
    }

    /**
     * POST /motos/ui
     * Cria uma moto a partir do form.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String criar(@ModelAttribute("moto") @Valid MotoRequest moto,
                        BindingResult binding,
                        RedirectAttributes ra) {
        if (binding.hasErrors()) return "motos/form";
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
                            RedirectAttributes ra) {
        if (binding.hasErrors()) return "motos/form";
        service.atualizar(id, moto);
        ra.addFlashAttribute("msgSucesso", "Moto atualizada com sucesso.");
        return "redirect:/motos/ui";
    }

    /**
     * POST /motos/ui/{id}/excluir
     * Exclui a moto e retorna para a lista.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        service.excluir(id);
        ra.addFlashAttribute("msgSucesso", "Moto excluída com sucesso.");
        return "redirect:/motos/ui";
    }
}
