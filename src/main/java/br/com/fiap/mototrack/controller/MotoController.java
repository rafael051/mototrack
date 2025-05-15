package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.MotoRequest;
import br.com.fiap.mototrack.dto.response.MotoResponse;
import br.com.fiap.mototrack.filter.MotoFilter;
import br.com.fiap.mototrack.service.MotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ## 🛵 Controller: MotoController
 *
 * Controlador responsável pelos endpoints REST da entidade Moto.
 * Permite cadastrar, listar, buscar, atualizar, deletar e filtrar registros de motocicletas.
 */
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Moto", description = "Endpoints relacionados ao controle de motocicletas da Mottu")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/motos")
@RequiredArgsConstructor
public class MotoController {

    private static final Logger log = LoggerFactory.getLogger(MotoController.class);
    private final MotoService service;

    /**
     * ### 🛵 POST /motos
     * Cadastra uma nova moto no sistema.
     */
    @PostMapping
    @CacheEvict(value = "motos", allEntries = true)
    @Operation(summary = "Cadastrar nova moto", description = "Registra uma nova moto no sistema da Mottu.")
    public ResponseEntity<MotoResponse> cadastrar(@RequestBody @Valid MotoRequest dto) {
        log.info("🛵 Cadastrando moto: {}", dto);
        return ResponseEntity.ok(service.cadastrar(dto));
    }

    /**
     * ### 📄 GET /motos
     * Lista todas as motos cadastradas.
     */
    @GetMapping
    @Cacheable("motos")
    @Operation(summary = "Listar todas as motos", description = "Retorna todas as motocicletas cadastradas no sistema.")
    public List<MotoResponse> listarTodas() {
        log.info("📄 Listando todas as motos.");
        return service.consultarTodos();
    }

    /**
     * ### 🔍 GET /motos/{id}
     * Retorna os dados de uma moto específica por ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar moto por ID", description = "Retorna os dados da moto correspondente ao ID informado.")
    public ResponseEntity<MotoResponse> buscarPorId(@PathVariable Long id) {
        log.info("🔍 Buscando moto ID: {}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * ### ✏️ PUT /motos/{id}
     * Atualiza os dados de uma moto específica.
     */
    @PutMapping("/{id}")
    @CacheEvict(value = "motos", allEntries = true)
    @Operation(summary = "Atualizar moto", description = "Atualiza os dados de uma motocicleta existente no sistema.")
    public ResponseEntity<MotoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid MotoRequest dto) {
        log.info("✏️ Atualizando moto ID: {}", id);
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * ### 🗑️ DELETE /motos/{id}
     * Remove uma moto do sistema.
     */
    @DeleteMapping("/{id}")
    @CacheEvict(value = "motos", allEntries = true)
    @Operation(summary = "Excluir moto", description = "Remove uma motocicleta do sistema com base no ID informado.")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        log.info("🗑️ Excluindo moto ID: {}", id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ### 🔍 GET /motos/filtro
     * Permite realizar buscas com filtros dinâmicos, paginação e ordenação.
     */
    @GetMapping("/filtro")
    @Operation(summary = "Filtrar motos com paginação e ordenação",
            description = "Permite aplicar filtros nos dados das motos com suporte a paginação e ordenação por query params.")
    public ResponseEntity<Page<MotoResponse>> filtrarComPaginacao(
            @ParameterObject @ModelAttribute MotoFilter filtro,

            @ParameterObject
            @PageableDefault(size = 20, sort = "placa", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        log.info("🗃️ Filtros aplicados: {}", filtro);
        return ResponseEntity.ok(service.consultarComFiltro(filtro, pageable));
    }
}