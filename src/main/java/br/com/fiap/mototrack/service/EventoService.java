package br.com.fiap.mototrack.service;

import br.com.fiap.mototrack.dto.request.EventoRequest;
import br.com.fiap.mototrack.dto.response.EventoResponse;
import br.com.fiap.mototrack.filter.EventoFilter;
import br.com.fiap.mototrack.model.Evento;
import br.com.fiap.mototrack.model.Moto;
import br.com.fiap.mototrack.repository.EventoRepository;
import br.com.fiap.mototrack.repository.MotoRepository;
import br.com.fiap.mototrack.specification.EventoSpecification;
import static br.com.fiap.mototrack.exception.HttpExceptionUtils.notFound;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;

/**
 * # 🛠️ Serviço: EventoService
 *
 * Camada de lógica de negócios da entidade `Evento`.
 *
 * ---
 * ## 📋 Responsabilidades:
 * - Conversão entre DTOs e entidades com ModelMapper
 * - Validação do relacionamento com Moto
 * - Registro, atualização, exclusão e consulta de eventos
 * - Consulta dinâmica via Specification
 * - Tratamento centralizado de exceções customizadas
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class EventoService {

    // =============================
    // 🔗 Injeção de Dependências
    // =============================

    private final EventoRepository repository;
    private final MotoRepository motoRepository;
    private final ModelMapper modelMapper;

    // =============================
    // 📝 Registrar novo evento
    // =============================

    /**
     * Cadastra um novo evento de movimentação, validando vínculo com a moto.
     */
    @Transactional
    public EventoResponse cadastrar(EventoRequest dto) {
        Evento evento = modelMapper.map(dto, Evento.class);

        if (dto.getMotoId() != null) {
            Moto moto = motoRepository.findById(dto.getMotoId())
                    .orElseThrow(() -> notFound("Moto", dto.getMotoId()));
            evento.setMoto(moto);
        }

        Evento salvo = repository.save(evento);
        return modelMapper.map(salvo, EventoResponse.class);
    }

    // =============================
    // ✏️ Atualizar evento existente
    // =============================

    /**
     * Atualiza os dados de um evento pelo ID.
     * Lança exceções se o evento ou a moto não forem encontrados.
     */
    @Transactional
    public EventoResponse atualizar(Long id, EventoRequest dto) {
        Evento existente = repository.findById(id)
                .orElseThrow(() -> notFound("Evento", id));

        modelMapper.map(dto, existente);

        if (dto.getMotoId() != null) {
            Moto moto = motoRepository.findById(dto.getMotoId())
                    .orElseThrow(() -> notFound("Moto", dto.getMotoId()));
            existente.setMoto(moto);
        } else {
            existente.setMoto(null);
        }

        Evento atualizado = repository.save(existente);
        return modelMapper.map(atualizado, EventoResponse.class);
    }

    // =============================
    // 📄 Consultar todos
    // =============================

    /**
     * Retorna todos os eventos registrados no sistema.
     */
    public List<EventoResponse> consultarTodos() {
        return repository.findAll().stream()
                .map(e -> modelMapper.map(e, EventoResponse.class))
                .toList();
    }

    // =============================
    // 🔍 Buscar por ID
    // =============================

    /**
     * Retorna os dados de um evento específico pelo seu ID.
     * Lança exceção se não encontrado.
     */
    public EventoResponse buscarPorId(Long id) {
        Evento evento = repository.findById(id)
                .orElseThrow(() -> notFound("Evento", id));
        return modelMapper.map(evento, EventoResponse.class);
    }

    // =============================
    // ❌ Excluir evento
    // =============================

    /**
     * Remove um evento pelo ID.
     * Lança exceção se o evento não existir.
     */
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw notFound("Evento", id);
        }
        repository.deleteById(id);
    }

    // =============================
    // 🔎 Consulta com filtros dinâmicos
    // =============================

    /**
     * Realiza uma busca paginada e ordenada de eventos com base nos filtros dinâmicos.
     */
    public Page<EventoResponse> consultarComFiltro(EventoFilter filtro, Pageable pageable) {
        var spec = EventoSpecification.comFiltros(filtro);
        return repository.findAll(spec, pageable)
                .map(e -> modelMapper.map(e, EventoResponse.class));
    }
}
