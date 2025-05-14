// 📦 service/MotoService.java
package br.com.fiap.mototrack.service;

import br.com.fiap.mototrack.dto.request.MotoRequest;
import br.com.fiap.mototrack.dto.response.MotoResponse;
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
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * # 🛠️ Serviço: MotoService
 *
 * Camada de regra de negócio da entidade Moto.
 * Responsável por:
 * - Converter DTO <-> Entidade
 * - Validar vínculos (ex: filial)
 * - Tratar exceções
 * - Delegar operações ao repositório com Specification
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class MotoService {

    // ============================
    // 🔗 Injeção de Dependências
    // ============================

    private final MotoRepository repository;
    private final FilialRepository filialRepository;
    private final ModelMapper modelMapper;

    // ============================
    // 📌 Cadastrar nova moto
    // ============================

    public MotoResponse cadastrar(MotoRequest dto) {
        Moto moto = modelMapper.map(dto, Moto.class);

        if (dto.getFilialId() != null) {
            Filial filial = filialRepository.findById(dto.getFilialId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Filial não encontrada"));
            moto.setFilial(filial);
        }

        Moto salva = repository.save(moto);
        return modelMapper.map(salva, MotoResponse.class);
    }

    // ============================
    // 🔁 Atualizar moto existente
    // ============================

    public MotoResponse atualizar(Long id, MotoRequest dto) {
        Moto existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Moto não encontrada"));

        modelMapper.map(dto, existente);

        if (dto.getFilialId() != null) {
            Filial filial = filialRepository.findById(dto.getFilialId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Filial não encontrada"));
            existente.setFilial(filial);
        } else {
            existente.setFilial(null);
        }

        Moto atualizada = repository.save(existente);
        return modelMapper.map(atualizada, MotoResponse.class);
    }

    // ============================
    // 🔍 Buscar por ID
    // ============================

    public MotoResponse buscarPorId(Long id) {
        Moto moto = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Moto não encontrada"));
        return modelMapper.map(moto, MotoResponse.class);
    }

    // ============================
    // ❌ Excluir moto
    // ============================

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Moto não encontrada para exclusão");
        }
        repository.deleteById(id);
    }

    // ============================
    // 🔍 Listar com filtros e paginação
    // ============================

    public Page<MotoResponse> consultarComFiltro(MotoFilter filtro, Pageable pageable) {
        var spec = MotoSpecification.comFiltros(filtro);
        return repository.findAll(spec, pageable)
                .map(moto -> modelMapper.map(moto, MotoResponse.class));
    }

}
