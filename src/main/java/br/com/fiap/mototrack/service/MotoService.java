package br.com.fiap.mototrack.service;

import br.com.fiap.mototrack.dto.request.MotoRequest;
import br.com.fiap.mototrack.dto.response.MotoResponse;
import br.com.fiap.mototrack.filter.MotoFilter;
import br.com.fiap.mototrack.model.Filial;
import br.com.fiap.mototrack.model.Moto;
import br.com.fiap.mototrack.repository.FilialRepository;
import br.com.fiap.mototrack.repository.MotoRepository;
import br.com.fiap.mototrack.specification.MotoSpecification;
import static br.com.fiap.mototrack.exception.HttpExceptionUtils.notFound;
import jakarta.transaction.Transactional;
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
 * ---
 * ## 📋 Responsabilidades:
 * - Conversão entre `DTO` e `Entity` com `ModelMapper`
 * - Validação de entidades relacionadas (como `Filial`)
 * - Aplicação de regras de negócio
 * - Utilização de Specifications para filtros dinâmicos
 * - Tratamento de exceções personalizadas
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
    // 📝 Cadastrar nova moto
    // =============================

    /**
     * Cadastra uma nova moto no sistema.
     * Valida o vínculo com filial, se informado.
     */
    @Transactional
    public MotoResponse cadastrar(MotoRequest dto) {
        Moto moto = modelMapper.map(dto, Moto.class);

        if (dto.getFilialId() != null) {
            Filial filial = filialRepository.findById(dto.getFilialId())
                    .orElseThrow(() -> notFound("Filial", dto.getFilialId()));
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
     * Lança exceções se a moto ou a filial não forem encontradas.
     */
    @Transactional
    public MotoResponse atualizar(Long id, MotoRequest dto) {
        Moto existente = repository.findById(id)
                .orElseThrow(() -> notFound("Moto", id));

        modelMapper.map(dto, existente);

        if (dto.getFilialId() != null) {
            Filial filial = filialRepository.findById(dto.getFilialId())
                    .orElseThrow(() -> notFound("Filial", dto.getFilialId()));
            existente.setFilial(filial);
        } else {
            existente.setFilial(null);
        }

        Moto atualizada = repository.save(existente);
        return modelMapper.map(atualizada, MotoResponse.class);
    }


    // =============================
    // 📄 Consultar todas as motos
    // =============================

    /**
     * Retorna todas as motos cadastradas no sistema.
     */
    public List<MotoResponse> consultarTodos() {
        return repository.findAll().stream()
                .map(m -> modelMapper.map(m, MotoResponse.class))
                .toList();
    }

    // =============================
    // 🔍 Buscar moto por ID
    // =============================

    /**
     * Retorna os dados de uma moto pelo ID.
     * Lança exceção se não encontrada.
     */
    public MotoResponse buscarPorId(Long id) {
        Moto moto = repository.findById(id)
                .orElseThrow(() -> notFound("Moto", id));
        return modelMapper.map(moto, MotoResponse.class);
    }


    // =============================
    // ❌ Excluir moto
    // =============================

    /**
     * Exclui uma moto com base no ID informado.
     * Lança exceção se a moto não existir.
     */
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw notFound("Moto", id);
        }
        repository.deleteById(id);
    }


    // =============================
    // 🔎 Consulta com filtros dinâmicos
    // =============================

    /**
     * Retorna uma página de motos com base nos filtros recebidos.
     */
    public Page<MotoResponse> consultarComFiltro(MotoFilter filtro, Pageable pageable) {
        var spec = MotoSpecification.comFiltros(filtro);
        return repository.findAll(spec, pageable)
                .map(moto -> modelMapper.map(moto, MotoResponse.class));
    }
}
