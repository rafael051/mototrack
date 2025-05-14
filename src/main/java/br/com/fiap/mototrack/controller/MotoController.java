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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ## 🛵 Controller: MotoController
 *
 * Controlador responsável pelos endpoints REST da entidade Moto.
 * Permite cadastrar, listar, buscar, atualizar, deletar e filtrar registros de motocicletas.
 */
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Moto", description = "Endpoints para controle de motocicletas nos pátios da Mottu")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/motos")
@RequiredArgsConstructor
public class MotoController {

    private final MotoService service;

    /**
     * ### POST /motos
     * Cadastra uma nova moto.
     */
    @PostMapping
    @CacheEvict(value = "motos", allEntries = true)
    @Operation(summary = "Cadastrar nova moto", description = "Registra uma nova moto no sistema da Mottu.")
    public ResponseEntity<MotoResponse> cadastrar(@RequestBody @Valid MotoRequest dto) {
        return ResponseEntity.ok(service.cadastrar(dto));
    }

    /**
     * ### GET /motos
     * Lista todas as motos com filtros opcionais, paginação e ordenação.
     */
    @GetMapping("/filtro")
    @Cacheable("motos")
    @Operation(summary = "Filtrar e listar motos", description = "Filtra motos com base nos parâmetros fornecidos e suporta paginação e ordenação.")
    public ResponseEntity<Page<MotoResponse>> listarComFiltro(
            @ParameterObject @ModelAttribute MotoFilter filtro,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "Campo de ordenação no formato: campo,direção (ex: placa,desc)",
                    example = "placa,desc")
            @RequestParam(defaultValue = "placa,desc") String sort
    ) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(offset, limit, Sort.by(direction, sortField));
        return ResponseEntity.ok(service.consultarComFiltro(filtro, pageable));
    }

    /**
     * ### GET /motos/{id}
     * Busca uma moto específica pelo ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar moto por ID", description = "Retorna os dados da moto correspondente ao ID informado.")
    public ResponseEntity<MotoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * ### PUT /motos/{id}
     * Atualiza uma moto existente pelo ID.
     */
    @PutMapping("/{id}")
    @CacheEvict(value = "motos", allEntries = true)
    @Operation(summary = "Atualizar moto", description = "Atualiza os dados de uma moto existente no sistema.")
    public ResponseEntity<MotoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid MotoRequest dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * ### DELETE /motos/{id}
     * Remove uma moto do sistema.
     */
    @DeleteMapping("/{id}")
    @CacheEvict(value = "motos", allEntries = true)
    @Operation(summary = "Excluir moto", description = "Remove uma moto do sistema com base no ID fornecido.")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
