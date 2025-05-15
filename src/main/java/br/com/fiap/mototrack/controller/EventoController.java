package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.EventoRequest;
import br.com.fiap.mototrack.dto.response.EventoResponse;
import br.com.fiap.mototrack.filter.EventoFilter;
import br.com.fiap.mototrack.service.EventoService;
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
 * ## 📑 Controller: EventoController
 *
 * Controlador responsável pelos endpoints REST da entidade Evento.
 * Permite cadastrar, listar, buscar, atualizar, deletar e filtrar registros de eventos de movimentação.
 */
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Evento", description = "Endpoints relacionados ao controle de eventos de movimentação de motos na Mottu")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private static final Logger log = LoggerFactory.getLogger(EventoController.class);
    private final EventoService service;

    /**
     * ### 📑 POST /eventos
     * Cadastra um novo evento no sistema.
     */
    @PostMapping
    @Operation(summary = "Cadastrar novo evento", description = "Registra um novo evento de movimentação no sistema da Mottu.")
    public ResponseEntity<EventoResponse> cadastrar(@RequestBody @Valid EventoRequest dto) {
        log.info("📑 Cadastrando evento: {}", dto);
        return ResponseEntity.ok(service.cadastrar(dto));
    }

    /**
     * ### 📄 GET /eventos
     * Lista todos os eventos cadastrados.
     */
    @GetMapping
    @Operation(summary = "Listar todos os eventos", description = "Retorna todos os eventos registrados no sistema.")
    public List<EventoResponse> listarTodos() {
        log.info("📄 Listando todos os eventos.");
        return service.consultarTodos();
    }

    /**
     * ### 🔍 GET /eventos/{id}
     * Retorna os dados de um evento específico por ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar evento por ID", description = "Retorna os dados do evento correspondente ao ID informado.")
    public ResponseEntity<EventoResponse> buscarPorId(@PathVariable Long id) {
        log.info("🔍 Buscando evento ID: {}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * ### ✏️ PUT /eventos/{id}
     * Atualiza os dados de um evento específico.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar evento", description = "Atualiza os dados de um evento existente no sistema.")
    public ResponseEntity<EventoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid EventoRequest dto) {
        log.info("✏️ Atualizando evento ID: {}", id);
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * ### 🗑️ DELETE /eventos/{id}
     * Remove um evento do sistema.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir evento", description = "Remove um evento do sistema com base no ID informado.")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        log.info("🗑️ Excluindo evento ID: {}", id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ### 🔍 GET /eventos/filtro
     * Permite realizar buscas com filtros dinâmicos, paginação e ordenação.
     */
    @GetMapping("/filtro")
    @Operation(summary = "Filtrar eventos com paginação e ordenação",
            description = "Permite aplicar filtros nos dados dos eventos com suporte a paginação e ordenação por query params.")
    public ResponseEntity<Page<EventoResponse>> filtrarComPaginacao(
            @ParameterObject @ModelAttribute EventoFilter filtro,
            @ParameterObject
            @PageableDefault(size = 20, sort = "dataHora", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.info("🗃️ Filtros aplicados: {}", filtro);
        return ResponseEntity.ok(service.consultarComFiltro(filtro, pageable));
    }
}
