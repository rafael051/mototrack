package br.com.fiap.mototrack.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 📦 DTO: EventoResponse
 * Representa os dados retornados após o registro de um evento.
 */
@Data
@NoArgsConstructor
public class EventoResponse {
    private Long id;
    private Long motoId;
    private String tipo;
    private String motivo;
    private LocalDateTime dataHora;
    private String localizacao;
}