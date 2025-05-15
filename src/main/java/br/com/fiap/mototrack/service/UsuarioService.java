package br.com.fiap.mototrack.service;

import br.com.fiap.mototrack.dto.request.UsuarioRequest;
import br.com.fiap.mototrack.dto.response.UsuarioResponse;
import br.com.fiap.mototrack.filter.UsuarioFilter;
import br.com.fiap.mototrack.model.Usuario;
import br.com.fiap.mototrack.repository.UsuarioRepository;
import br.com.fiap.mototrack.specification.UsuarioSpecification;
import static br.com.fiap.mototrack.exception.HttpExceptionUtils.notFound;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * # 🛠️ Serviço: UsuarioService
 *
 * Camada responsável pela lógica de negócio da entidade `Usuario`.
 *
 * ---
 * ## 📋 Responsabilidades:
 * - Cadastro, edição e exclusão de usuários do sistema
 * - Conversão entre DTOs e entidades com ModelMapper
 * - Consultas dinâmicas com Specification
 * - Tratamento de exceções centralizadas e amigáveis
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    // =============================
    // 🔗 Injeção de Dependências
    // =============================

    private final UsuarioRepository repository;
    private final ModelMapper modelMapper;

    // =============================
    // 📝 Cadastrar novo usuário
    // =============================

    /**
     * Cadastra um novo usuário no sistema.
     */
    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest dto) {
        Usuario usuario = modelMapper.map(dto, Usuario.class);
        Usuario salvo = repository.save(usuario);
        return modelMapper.map(salvo, UsuarioResponse.class);
    }

    // =============================
    // ✏️ Atualizar usuário
    // =============================

    /**
     * Atualiza os dados de um usuário existente.
     * Lança exceção se não encontrado.
     */
    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest dto) {
        Usuario existente = repository.findById(id)
                .orElseThrow(() -> notFound("Usuario", id));

        modelMapper.map(dto, existente);
        Usuario atualizado = repository.save(existente);
        return modelMapper.map(atualizado, UsuarioResponse.class);
    }

    // =============================
    // 📄 Listar todos os usuários
    // =============================

    /**
     * Retorna todos os usuários cadastrados no sistema.
     */
    public List<UsuarioResponse> consultarTodos() {
        return repository.findAll().stream()
                .map(u -> modelMapper.map(u, UsuarioResponse.class))
                .toList();
    }

    // =============================
    // 🔍 Buscar por ID
    // =============================

    /**
     * Retorna os dados de um usuário específico.
     * Lança exceção se não encontrado.
     */
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> notFound("Usuario", id));
        return modelMapper.map(usuario, UsuarioResponse.class);
    }

    // =============================
    // ❌ Excluir usuário
    // =============================

    /**
     * Remove um usuário com base no ID.
     * Lança exceção se não encontrado.
     */
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw notFound("Usuario", id);
        }
        repository.deleteById(id);
    }

    // =============================
    // 🔎 Consultar com filtros dinâmicos
    // =============================

    /**
     * Realiza consulta paginada e com filtros para usuários.
     */
    public Page<UsuarioResponse> consultarComFiltro(UsuarioFilter filtro, Pageable pageable) {
        var spec = UsuarioSpecification.comFiltros(filtro);
        return repository.findAll(spec, pageable)
                .map(u -> modelMapper.map(u, UsuarioResponse.class));
    }
}
