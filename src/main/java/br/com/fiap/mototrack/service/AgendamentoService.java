package br.com.fiap.mototrack.service;

import br.com.fiap.mototrack.dto.request.AgendamentoRequest;
import br.com.fiap.mototrack.dto.response.AgendamentoResponse;
import br.com.fiap.mototrack.filter.AgendamentoFilter;
import br.com.fiap.mototrack.model.Agendamento;
import br.com.fiap.mototrack.model.Moto;
import br.com.fiap.mototrack.repository.AgendamentoRepository;
import br.com.fiap.mototrack.repository.MotoRepository;
import br.com.fiap.mototrack.specification.AgendamentoSpecification;
import static br.com.fiap.mototrack.exception.HttpExceptionUtils.notFound;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * # 🛠️ Serviço: AgendamentoService
 *
 * Camada de lógica de negócios da entidade `Agendamento`.
 *
 * ---
 * ## 📋 Responsabilidades:
 * - Conversão entre DTOs e entidades com ModelMapper
 * - Validação do vínculo com Moto
 * - Registro, atualização, exclusão e consulta de agendamentos
 * - Suporte a filtros dinâmicos e paginação via Specification
 * - Tratamento centralizado de exceções customizadas
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AgendamentoService {

    // =============================
    // 🔗 Injeção de Dependências
    // =============================

    private final AgendamentoRepository agendamentoRepository;
    private final MotoRepository motoRepository;
    private final ModelMapper modelMapper;

    // =============================
    // 📝 Cadastrar novo agendamento
    // =============================

    /**
     * Cadastra um novo agendamento, validando o vínculo com a moto.
     */
    @Transactional
    public AgendamentoResponse cadastrar(AgendamentoRequest dto) {
        Agendamento agendamento = modelMapper.map(dto, Agendamento.class);

        if (dto.getMotoId() != null) {
            Moto moto = motoRepository.findById(dto.getMotoId())
                    .orElseThrow(() -> notFound("Moto", dto.getMotoId()));
            agendamento.setMoto(moto);
        }

        Agendamento salvo = agendamentoRepository.save(agendamento);
        return modelMapper.map(salvo, AgendamentoResponse.class);
    }

    // =============================
    // ✏️ Atualizar agendamento
    // =============================

    /**
     * Atualiza os dados de um agendamento pelo ID, validando a moto se informada.
     */
    @Transactional
    public AgendamentoResponse atualizar(Long id, AgendamentoRequest dto) {
        Agendamento existente = agendamentoRepository.findById(id)
                .orElseThrow(() -> notFound("Agendamento", id));

        modelMapper.map(dto, existente);

        if (dto.getMotoId() != null) {
            Moto moto = motoRepository.findById(dto.getMotoId())
                    .orElseThrow(() -> notFound("Moto", dto.getMotoId()));
            existente.setMoto(moto);
        } else {
            existente.setMoto(null);
        }

        Agendamento atualizado = agendamentoRepository.save(existente);
        return modelMapper.map(atualizado, AgendamentoResponse.class);
    }

    // =============================
    // 📄 Consultar todos
    // =============================

    /**
     * Retorna todos os agendamentos cadastrados.
     */
    public List<AgendamentoResponse> consultarTodos() {
        return agendamentoRepository.findAll().stream()
                .map(a -> modelMapper.map(a, AgendamentoResponse.class))
                .toList();
    }

    // =============================
    // 🔍 Buscar por ID
    // =============================

    /**
     * Retorna um agendamento pelo ID.
     * Lança exceção se não encontrado.
     */
    public AgendamentoResponse buscarPorId(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> notFound("Agendamento", id));
        return modelMapper.map(agendamento, AgendamentoResponse.class);
    }

    // =============================
    // ❌ Excluir agendamento
    // =============================

    /**
     * Exclui um agendamento pelo ID.
     * Lança exceção se não existir.
     */
    @Transactional
    public void excluir(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw notFound("Agendamento", id);
        }
        agendamentoRepository.deleteById(id);
    }

    // =============================
    // 🔎 Consulta com filtros dinâmicos
    // =============================

    /**
     * Retorna uma página de agendamentos aplicando filtros dinâmicos.
     */
    public Page<AgendamentoResponse> consultarComFiltro(AgendamentoFilter filtro, Pageable pageable) {
        var spec = AgendamentoSpecification.comFiltros(filtro);
        return agendamentoRepository.findAll(spec, pageable)
                .map(a -> modelMapper.map(a, AgendamentoResponse.class));
    }
}
