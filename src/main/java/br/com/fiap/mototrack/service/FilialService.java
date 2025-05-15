package br.com.fiap.mototrack.service;

import br.com.fiap.mototrack.dto.request.FilialRequest;
import br.com.fiap.mototrack.dto.response.FilialResponse;
import br.com.fiap.mototrack.filter.FilialFilter;
import br.com.fiap.mototrack.model.Filial;
import br.com.fiap.mototrack.repository.FilialRepository;
import br.com.fiap.mototrack.specification.FilialSpecification;
import static br.com.fiap.mototrack.exception.HttpExceptionUtils.notFound;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * # 🛠️ Serviço: FilialService
 *
 * Camada de serviço responsável pelas regras de negócio da entidade `Filial`,
 * que representa unidades físicas da Mottu.
 *
 * ---
 * ## 📋 Responsabilidades:
 * - Cadastro e atualização de filiais
 * - Consulta por ID, listagem geral e com filtros dinâmicos
 * - Conversão entre DTOs e entidades
 * - Tratamento de exceções centralizadas e amigáveis
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class FilialService {

    // =============================
    // 🔗 Injeção de Dependências
    // =============================

    private final FilialRepository repository;
    private final ModelMapper modelMapper;

    // =============================
    // 📝 Criar nova filial
    // =============================

    /**
     * Cadastra uma nova filial no sistema.
     */
    @Transactional
    public FilialResponse cadastrar(FilialRequest dto) {
        Filial nova = modelMapper.map(dto, Filial.class);
        Filial salva = repository.save(nova);
        return modelMapper.map(salva, FilialResponse.class);
    }

    // =============================
    // ✏️ Atualizar filial
    // =============================

    /**
     * Atualiza os dados de uma filial com base no ID.
     * Lança exceção se não encontrada.
     */
    @Transactional
    public FilialResponse atualizar(Long id, FilialRequest dto) {
        Filial existente = repository.findById(id)
                .orElseThrow(() -> notFound("Filial", id));

        modelMapper.map(dto, existente);
        Filial atualizada = repository.save(existente);
        return modelMapper.map(atualizada, FilialResponse.class);
    }

    // =============================
    // 📄 Listar todas as filiais
    // =============================

    /**
     * Retorna a lista de todas as filiais cadastradas.
     */
    public List<FilialResponse> consultarTodos() {
        return repository.findAll().stream()
                .map(f -> modelMapper.map(f, FilialResponse.class))
                .toList();
    }

    // =============================
    // 🔍 Buscar filial por ID
    // =============================

    /**
     * Retorna os dados de uma filial específica pelo seu ID.
     * Lança exceção se não encontrada.
     */
    public FilialResponse buscarPorId(Long id) {
        Filial filial = repository.findById(id)
                .orElseThrow(() -> notFound("Filial", id));
        return modelMapper.map(filial, FilialResponse.class);
    }

    // =============================
    // ❌ Excluir filial
    // =============================

    /**
     * Exclui uma filial do sistema com base no ID.
     * Lança exceção se não existir.
     */
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw notFound("Filial", id);
        }
        repository.deleteById(id);
    }

    // =============================
    // 🔎 Consulta com filtros dinâmicos
    // =============================

    /**
     * Consulta paginada de filiais com suporte a filtros dinâmicos.
     */
    public Page<FilialResponse> consultarComFiltro(FilialFilter filtro, Pageable pageable) {
        var spec = FilialSpecification.comFiltros(filtro);
        return repository.findAll(spec, pageable)
                .map(f -> modelMapper.map(f, FilialResponse.class));
    }
}
