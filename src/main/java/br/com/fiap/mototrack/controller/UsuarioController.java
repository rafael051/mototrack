package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.UsuarioRequest;
import br.com.fiap.mototrack.dto.response.UsuarioResponse;
import br.com.fiap.mototrack.filter.UsuarioFilter;
import br.com.fiap.mototrack.service.UsuarioService;
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
 * ## 👤 Controller: UsuarioController
 *
 * Controlador responsável pelos endpoints REST da entidade Usuario.
 * Permite cadastrar, listar, buscar, atualizar, deletar e filtrar registros de usuários do sistema.
 */
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuario", description = "Endpoints relacionados ao controle de usuários do sistema Mottu")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService service;

    /**
     * ### 👤 POST /usuarios
     * Cadastra um novo usuário no sistema.
     */
    @PostMapping
    @Operation(summary = "Cadastrar novo usuário", description = "Registra um novo usuário no sistema da Mottu.")
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioRequest dto) {
        log.info("👤 Cadastrando usuário: {}", dto);
        return ResponseEntity.ok(service.cadastrar(dto));
    }

    /**
     * ### 📄 GET /usuarios
     * Lista todos os usuários cadastrados.
     */
    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna todos os usuários cadastrados no sistema.")
    public List<UsuarioResponse> listarTodos() {
        log.info("📄 Listando todos os usuários.");
        return service.consultarTodos();
    }

    /**
     * ### 🔍 GET /usuarios/{id}
     * Retorna os dados de um usuário específico por ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados do usuário correspondente ao ID informado.")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        log.info("🔍 Buscando usuário ID: {}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * ### ✏️ PUT /usuarios/{id}
     * Atualiza os dados de um usuário específico.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente no sistema.")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequest dto) {
        log.info("✏️ Atualizando usuário ID: {}", id);
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * ### 🗑️ DELETE /usuarios/{id}
     * Remove um usuário do sistema.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Remove um usuário do sistema com base no ID informado.")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        log.info("🗑️ Excluindo usuário ID: {}", id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ### 🔍 GET /usuarios/filtro
     * Permite realizar buscas com filtros dinâmicos, paginação e ordenação.
     */
    @GetMapping("/filtro")
    @Operation(summary = "Filtrar usuários com paginação e ordenação",
            description = "Permite aplicar filtros nos dados dos usuários com suporte a paginação e ordenação por query params.")
    public ResponseEntity<Page<UsuarioResponse>> filtrarComPaginacao(
            @ParameterObject @ModelAttribute UsuarioFilter filtro,
            @ParameterObject
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        log.info("🗃️ Filtros aplicados: {}", filtro);
        return ResponseEntity.ok(service.consultarComFiltro(filtro, pageable));
    }
}
