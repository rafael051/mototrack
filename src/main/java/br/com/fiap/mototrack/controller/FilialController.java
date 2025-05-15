package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.FilialRequest;
import br.com.fiap.mototrack.dto.response.FilialResponse;
import br.com.fiap.mototrack.filter.FilialFilter;
import br.com.fiap.mototrack.service.FilialService;
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
 * ## 🏢 Controller: FilialController
 *
 * Controlador responsável pelos endpoints REST da entidade Filial.
 * Permite cadastrar, listar, buscar, atualizar, deletar e filtrar registros de filiais.
 */
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Filial", description = "Endpoints relacionados ao controle de filiais (unidades Mottu)")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/filiais")
@RequiredArgsConstructor
public class FilialController {

    private static final Logger log = LoggerFactory.getLogger(FilialController.class);
    private final FilialService service;

    /**
     * ### 🏢 POST /filiais
     * Cadastra uma nova filial no sistema.
     */
    @PostMapping
    @Operation(summary = "Cadastrar nova filial", description = "Registra uma nova filial (unidade) no sistema da Mottu.")
    public ResponseEntity<FilialResponse> cadastrar(@RequestBody @Valid FilialRequest dto) {
        log.info("🏢 Cadastrando filial: {}", dto);
        return ResponseEntity.ok(service.cadastrar(dto));
    }

    /**
     * ### 📄 GET /filiais
     * Lista todas as filiais cadastradas.
     */
    @GetMapping
    @Operation(summary = "Listar todas as filiais", description = "Retorna todas as filiais cadastradas no sistema.")
    public List<FilialResponse> listarTodas() {
        log.info("📄 Listando todas as filiais.");
        return service.consultarTodos();
    }

    /**
     * ### 🔍 GET /filiais/{id}
     * Retorna os dados de uma filial específica por ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar filial por ID", description = "Retorna os dados da filial correspondente ao ID informado.")
    public ResponseEntity<FilialResponse> buscarPorId(@PathVariable Long id) {
        log.info("🔍 Buscando filial ID: {}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * ### ✏️ PUT /filiais/{id}
     * Atualiza os dados de uma filial específica.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar filial", description = "Atualiza os dados de uma filial existente no sistema.")
    public ResponseEntity<FilialResponse> atualizar(@PathVariable Long id, @RequestBody @Valid FilialRequest dto) {
        log.info("✏️ Atualizando filial ID: {}", id);
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * ### 🗑️ DELETE /filiais/{id}
     * Remove uma filial do sistema.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir filial", description = "Remove uma filial do sistema com base no ID informado.")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        log.info("🗑️ Excluindo filial ID: {}", id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ### 🔍 GET /filiais/filtro
     * Permite realizar buscas com filtros dinâmicos, paginação e ordenação.
     */
    @GetMapping("/filtro")
    @Operation(summary = "Filtrar filiais com paginação e ordenação",
            description = "Permite aplicar filtros nos dados das filiais com suporte a paginação e ordenação por query params.")
    public ResponseEntity<Page<FilialResponse>> filtrarComPaginacao(
            @ParameterObject @ModelAttribute FilialFilter filtro,
            @ParameterObject
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        log.info("🗃️ Filtros aplicados: {}", filtro);
        return ResponseEntity.ok(service.consultarComFiltro(filtro, pageable));
    }
}
