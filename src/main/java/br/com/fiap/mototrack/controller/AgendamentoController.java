package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.AgendamentoRequest;
import br.com.fiap.mototrack.dto.response.AgendamentoResponse;
import br.com.fiap.mototrack.filter.AgendamentoFilter;
import br.com.fiap.mototrack.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ## 📅 Controller: AgendamentoController
 *
 * Controlador responsável pelos endpoints REST da entidade Agendamento.
 * Permite cadastrar, listar, buscar, atualizar, deletar e filtrar registros de agendamentos.
 */
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Agendamento", description = "Endpoints relacionados ao controle de agendamentos de motos na Mottu")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoController.class);
    private final AgendamentoService service;

    /**
     * ### 📅 POST /agendamentos
     * Cadastra um novo agendamento no sistema.
     */
    @PostMapping
    @Operation(summary = "Cadastrar novo agendamento", description = "Registra um novo agendamento no sistema da Mottu.")
    public ResponseEntity<AgendamentoResponse> cadastrar(@RequestBody @Valid AgendamentoRequest dto) {
        log.info("📅 Cadastrando agendamento: {}", dto);
        return ResponseEntity.ok(service.cadastrar(dto));
    }

    /**
     * ### 📄 GET /agendamentos
     * Lista todos os agendamentos cadastrados.
     */
    @GetMapping
    @Operation(summary = "Listar todos os agendamentos", description = "Retorna todos os agendamentos cadastrados no sistema.")
    public List<AgendamentoResponse> listarTodos() {
        log.info("📄 Listando todos os agendamentos.");
        return service.consultarTodos();
    }

    /**
     * ### 🔍 GET /agendamentos/{id}
     * Retorna os dados de um agendamento específico por ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID", description = "Retorna os dados do agendamento correspondente ao ID informado.")
    public ResponseEntity<AgendamentoResponse> buscarPorId(@PathVariable Long id) {
        log.info("🔍 Buscando agendamento ID: {}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * ### ✏️ PUT /agendamentos/{id}
     * Atualiza os dados de um agendamento específico.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar agendamento", description = "Atualiza os dados de um agendamento existente no sistema.")
    public ResponseEntity<AgendamentoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid AgendamentoRequest dto) {
        log.info("✏️ Atualizando agendamento ID: {}", id);
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * ### 🗑️ DELETE /agendamentos/{id}
     * Remove um agendamento do sistema.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir agendamento", description = "Remove um agendamento do sistema com base no ID informado.")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        log.info("🗑️ Excluindo agendamento ID: {}", id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ### 🔍 GET /agendamentos/filtro
     * Permite realizar buscas com filtros dinâmicos, paginação e ordenação.
     */
    @GetMapping("/filtro")
    @Operation(summary = "Filtrar agendamentos com paginação e ordenação",
            description = "Permite aplicar filtros nos dados dos agendamentos com suporte a paginação e ordenação por query params.")
    public ResponseEntity<Page<AgendamentoResponse>> filtrarComPaginacao(
            @ParameterObject @ModelAttribute AgendamentoFilter filtro,
            @ParameterObject
            @PageableDefault(size = 20, sort = "dataHora", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        log.info("🗃️ Filtros aplicados: {}", filtro);
        return ResponseEntity.ok(service.consultarComFiltro(filtro, pageable));
    }
}
