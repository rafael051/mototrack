package br.com.fiap.mototrack.service;

import br.com.fiap.mototrack.dto.request.MotoRequest;
import br.com.fiap.mototrack.dto.response.MotoResponse;
import br.com.fiap.mototrack.exception.FilialNotFoundException;
import br.com.fiap.mototrack.exception.MotoNotFoundException;
import br.com.fiap.mototrack.filter.MotoFilter;
import br.com.fiap.mototrack.model.Filial;
import br.com.fiap.mototrack.model.Moto;
import br.com.fiap.mototrack.repository.FilialRepository;
import br.com.fiap.mototrack.repository.MotoRepository;
import br.com.fiap.mototrack.specification.MotoSpecification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * # 🛠️ Serviço: MotoService
 *
 * Camada de lógica de negócios da entidade `Moto`.
 *
 * ## 📋 Responsabilidades:
 * - Conversão entre `DTO` e `Entity` usando `ModelMapper`
 * - Validação de entidades relacionadas (como `Filial`)
 * - Aplicação de regras e delegação para o repositório
 * - Utilização de Specifications para consultas dinâmicas
 * - Tratamento de exceções com mensagens claras e centralizadas
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class MotoService {

    // =============================
    // 🔗 Injeção de Dependências
    // =============================

    private final MotoRepository repository;
    private final FilialRepository filialRepository;
    private final ModelMapper modelMapper;

    // =============================
    // 📌 Cadastrar nova moto
    // =============================

    /**
     * Cadastra uma nova moto no banco de dados.
     * Valida o vínculo com filial (se fornecido) e mapeia o DTO para entidade.
     */
    public MotoResponse cadastrar(MotoRequest dto) {
        Moto moto = modelMapper.map(dto, Moto.class);

        // 🔗 Validação do relacionamento com a Filial
        if (dto.getFilialId() != null) {
            Filial filial = filialRepository.findById(dto.getFilialId())
                    .orElseThrow(FilialNotFoundException::new);
            moto.setFilial(filial);
        }

        Moto salva = repository.save(moto);
        return modelMapper.map(salva, MotoResponse.class);
    }

    // =============================
    // ✏️ Atualizar moto existente
    // =============================

    /**
     * Atualiza os dados de uma moto com base no ID fornecido.
     * Lança exceção se a moto ou filial não forem encontradas.
     */
    public MotoResponse atualizar(Long id, MotoRequest dto) {
        Moto existente = repository.findById(id)
                .orElseThrow(MotoNotFoundException::new);

        modelMapper.map(dto, existente);

        if (dto.getFilialId() != null) {
            Filial filial = filialRepository.findById(dto.getFilialId())
                    .orElseThrow(FilialNotFoundException::new);
            existente.setFilial(filial);
        } else {
            existente.setFilial(null); // Limpa vínculo se não informado
        }

        Moto atualizada = repository.save(existente);
        return modelMapper.map(atualizada, MotoResponse.class);
    }

    // =============================
    // 📄 Consultar todas
    // =============================

    /**
     * Lista todas as motos da base, sem filtros.
     */
    public List<MotoResponse> consultarTodos() {
        return repository.findAll().stream()
                .map(m -> modelMapper.map(m, MotoResponse.class))
                .toList();
    }

    // =============================
    // 🔍 Buscar por ID
    // =============================

    /**
     * Retorna os dados de uma moto específica pelo seu ID.
     */
    public MotoResponse buscarPorId(Long id) {
        Moto moto = repository.findById(id)
                .orElseThrow(MotoNotFoundException::new);
        return modelMapper.map(moto, MotoResponse.class);
    }

    // =============================
    // ❌ Excluir moto
    // =============================

    /**
     * Exclui uma moto do banco com base no ID informado.
     * Lança exceção se a moto não existir.
     */
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new MotoNotFoundException("Moto não encontrada para exclusão");
        }
        repository.deleteById(id);
    }

    // =============================
    // 🔎 Consulta com Filtros
    // =============================

    /**
     * Retorna uma página de resultados de motos filtradas dinamicamente
     * com paginação e ordenação, via Specification.
     */
    public Page<MotoResponse> consultarComFiltro(MotoFilter filtro, Pageable pageable) {
        var spec = MotoSpecification.comFiltros(filtro);
        return repository.findAll(spec, pageable)
                .map(moto -> modelMapper.map(moto, MotoResponse.class));
    }
}
