package br.com.fiap.mototrack.controller;

import br.com.fiap.mototrack.dto.request.UsuarioRequest;
import br.com.fiap.mototrack.dto.response.UsuarioResponse;
import br.com.fiap.mototrack.filter.UsuarioFilter;
import br.com.fiap.mototrack.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ✅ proteção por perfil (Sprint 3)
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * ## 👤 Controller: UsuarioController
 *
 * Controlador REST responsável pelos endpoints da entidade <b>Usuario</b>.
 * Oferece operações de criação, consulta, atualização, exclusão e busca com filtros dinâmicos.
 *
 * ### ✅ Adequações Sprint 3 (Segurança e Boas Práticas)
 * - Proteção por perfil (roles) com {@link PreAuthorize}: ADMIN e USER.
 * - Documentação com OpenAPI/Swagger e exigência de segurança ({@link SecurityRequirement}).
 *   > Observação: Se estiver usando sessão (form login), o "cadeado" no Swagger indica proteção,
 *   > mas a autenticação acontece via sessão do navegador. Se adotar JWT depois, mantenha "bearerAuth".
 * - Respostas padronizadas com {@link ResponseEntity}.
 * - Paginação e ordenação nos endpoints de consulta.
 *
 * ### 🔐 Papéis/Autorização
 * - ADMIN: criar, atualizar e excluir usuários.
 * - USER/ADMIN: consultar (listar, buscar por ID, consultar com filtros).
 *
 * ### 📌 Observações Importantes
 * - Evite logar dados sensíveis (ex.: senha). Aqui os logs são informativos e não imprimem o DTO inteiro.
 * - Mantenha {@code @EnableMethodSecurity(prePostEnabled = true)} no SecurityConfig para habilitar @PreAuthorize.
 * - Por padrão, {@code hasRole('ADMIN')} verifica a authority {@code ROLE_ADMIN}.
 *
 * @author
 *   Equipe MotoTrack — Mottu
 */
@Validated
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuario", description = "Endpoints relacionados ao controle de usuários do sistema Mottu")
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(value = "/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService service;

    /**
     * ### 👤 POST /usuarios
     * Cadastra um novo usuário no sistema.
     *
     * #### Regras de Segurança
     * - 🔒 Requer perfil <b>ADMIN</b>.
     *
     * #### Validações
     * - O corpo da requisição é validado com Bean Validation em {@link UsuarioRequest}.
     *
     * #### Respostas
     * - <b>201 Created</b> com o objeto criado no corpo e cabeçalho <b>Location</b> apontando para <code>/usuarios/{id}</code>.
     * - <b>400 Bad Request</b> para erros de validação.
     * - <b>401/403</b> quando o token é inválido/ausente ou o perfil não tem permissão.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cadastrar novo usuário", description = "Registra um novo usuário no sistema da Mottu.")
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioRequest dto) {
        log.info("👤 Solicitada criação de usuário");
        UsuarioResponse salvo = service.cadastrar(dto);
        URI location = URI.create("/usuarios/" + salvo.getId());
        return ResponseEntity.created(location).body(salvo);
    }

    /**
     * ### 📄 GET /usuarios
     * Lista todos os usuários cadastrados (não paginado).
     *
     * #### Regras de Segurança
     * - 🔒 Requer perfil <b>USER</b> ou <b>ADMIN</b>.
     *
     * #### Respostas
     * - <b>200 OK</b> com a lista de usuários.
     * - <b>401/403</b> quando o token é inválido/ausente ou o perfil não tem permissão.
     *
     * #### Nota
     * - Para grandes volumes, considere expor também um endpoint paginado (ex.: <code>/usuarios/filtro</code>).
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna todos os usuários cadastrados no sistema.")
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        log.info("📄 Listando todos os usuários");
        return ResponseEntity.ok(service.consultarTodos());
    }

    /**
     * ### 🔍 GET /usuarios/{id}
     * Busca os dados de um usuário específico pelo identificador.
     *
     * #### Regras de Segurança
     * - 🔒 Requer perfil <b>USER</b> ou <b>ADMIN</b>.
     *
     * #### Respostas
     * - <b>200 OK</b> com o usuário encontrado.
     * - <b>404 Not Found</b> se o usuário não existir.
     * - <b>401/403</b> para problemas de autenticação/autorização.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados do usuário correspondente ao ID informado.")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        log.info("🔍 Buscando usuário ID: {}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * ### ✏️ PUT /usuarios/{id}
     * Atualiza os dados de um usuário existente.
     *
     * #### Regras de Segurança
     * - 🔒 Requer perfil <b>ADMIN</b>.
     *
     * #### Validações
     * - O corpo da requisição é validado com Bean Validation em {@link UsuarioRequest}.
     *
     * #### Respostas
     * - <b>200 OK</b> com o usuário atualizado.
     * - <b>400 Bad Request</b> para erros de validação.
     * - <b>404 Not Found</b> se o usuário não existir.
     * - <b>401/403</b> para problemas de autenticação/autorização.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente no sistema.")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequest dto) {
        log.info("✏️ Atualizando usuário ID: {}", id);
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    /**
     * ### 🗑️ DELETE /usuarios/{id}
     * Remove um usuário do sistema pelo identificador.
     *
     * #### Regras de Segurança
     * - 🔒 Requer perfil <b>ADMIN</b>.
     *
     * #### Respostas
     * - <b>204 No Content</b> em caso de exclusão bem-sucedida.
     * - <b>404 Not Found</b> se o usuário não existir.
     * - <b>401/403</b> para problemas de autenticação/autorização.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Remove um usuário do sistema com base no ID informado.")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        log.info("🗑️ Excluindo usuário ID: {}", id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ### 🔍 GET /usuarios/filtro
     * Consulta usuários aplicando filtros dinâmicos com suporte a paginação e ordenação.
     *
     * #### Regras de Segurança
     * - 🔒 Requer perfil <b>USER</b> ou <b>ADMIN</b>.
     *
     * #### Filtros Suportados (exemplos)
     * - Campos do {@link UsuarioFilter} (ex.: nome, email, status, datas).
     * - Paginação via {@link Pageable}: <code>size</code>, <code>page</code>.
     * - Ordenação via <code>sort=campo,ASC|DESC</code>.
     *
     * #### Exemplos de Uso
     * - <code>/usuarios/filtro?nome=ana&email=@empresa.com&size=10&page=0&sort=nome,desc</code>
     *
     * #### Respostas
     * - <b>200 OK</b> com um {@link Page} de {@link UsuarioResponse}.
     * - <b>401/403</b> para problemas de autenticação/autorização.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/filtro")
    @Operation(
            summary = "Filtrar usuários com paginação e ordenação",
            description = "Permite aplicar filtros nos dados dos usuários com suporte a paginação e ordenação por query params."
    )
    public ResponseEntity<Page<UsuarioResponse>> filtrarComPaginacao(
            @ParameterObject @ModelAttribute UsuarioFilter filtro,
            @ParameterObject
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        log.info("🗃️ Consulta com filtros: {}", filtro);
        return ResponseEntity.ok(service.consultarComFiltro(filtro, pageable));
    }
}
