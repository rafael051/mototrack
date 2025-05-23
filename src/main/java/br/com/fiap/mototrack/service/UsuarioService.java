package br.com.fiap.mototrack.service;

import br.com.fiap.mototrack.dto.request.UsuarioRequest;
import br.com.fiap.mototrack.dto.response.UsuarioResponse;
import br.com.fiap.mototrack.filter.UsuarioFilter;
import br.com.fiap.mototrack.model.Filial;
import br.com.fiap.mototrack.model.Usuario;
import br.com.fiap.mototrack.repository.FilialRepository;
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
 * - Validação e vinculação da filial ao usuário
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
    private final FilialRepository filialRepository;
    private final ModelMapper modelMapper;

    // =============================
    // 📝 Cadastrar novo usuário
    // =============================

    /**
     * Cadastra um novo usuário no sistema.
     * Faz o vínculo com a filial se o campo filialId for informado.
     */
    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest dto) {
        Usuario usuario = modelMapper.map(dto, Usuario.class);

        // Se foi informado o ID da filial, vincula ao usuário
        if (dto.getFilialId() != null) {
            Filial filial = filialRepository.findById(dto.getFilialId())
                    .orElseThrow(() -> notFound("Filial", dto.getFilialId()));
            usuario.setFilial(filial);
        } else {
            usuario.setFilial(null);
        }

        Usuario salvo = repository.save(usuario);

        // Prepara o response já incluindo o filialId, se houver
        UsuarioResponse response = modelMapper.map(salvo, UsuarioResponse.class);
        if (salvo.getFilial() != null) {
            response.setFilialId(salvo.getFilial().getId());
        }
        return response;
    }

    // =============================
    // ✏️ Atualizar usuário
    // =============================

    /**
     * Atualiza os dados de um usuário existente.
     * Lança exceção se não encontrado.
     * Atualiza o vínculo com filial, se informado.
     */
    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest dto) {
        Usuario existente = repository.findById(id)
                .orElseThrow(() -> notFound("Usuario", id));

        modelMapper.map(dto, existente);

        if (dto.getFilialId() != null) {
            Filial filial = filialRepository.findById(dto.getFilialId())
                    .orElseThrow(() -> notFound("Filial", dto.getFilialId()));
            existente.setFilial(filial);
        } else {
            existente.setFilial(null);
        }

        Usuario atualizado = repository.save(existente);

        UsuarioResponse response = modelMapper.map(atualizado, UsuarioResponse.class);
        if (atualizado.getFilial() != null) {
            response.setFilialId(atualizado.getFilial().getId());
        }
        return response;
    }

    // =============================
    // 📄 Listar todos os usuários
    // =============================

    /**
     * Retorna todos os usuários cadastrados no sistema.
     * Inclui o ID da filial associada, se houver.
     */
    public List<UsuarioResponse> consultarTodos() {
        return repository.findAll().stream()
                .map(u -> {
                    UsuarioResponse resp = modelMapper.map(u, UsuarioResponse.class);
                    if (u.getFilial() != null) {
                        resp.setFilialId(u.getFilial().getId());
                    }
                    return resp;
                })
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
        UsuarioResponse response = modelMapper.map(usuario, UsuarioResponse.class);
        if (usuario.getFilial() != null) {
            response.setFilialId(usuario.getFilial().getId());
        }
        return response;
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
                .map(u -> {
                    UsuarioResponse resp = modelMapper.map(u, UsuarioResponse.class);
                    if (u.getFilial() != null) {
                        resp.setFilialId(u.getFilial().getId());
                    }
                    return resp;
                });
    }
}
