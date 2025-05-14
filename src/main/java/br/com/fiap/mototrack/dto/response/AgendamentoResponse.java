package br.com.fiap.mototrack.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 📦 DTO: AgendamentoResponse
 * Representa os dados de um agendamento retornado pela API.
 */
@Data
@NoArgsConstructor
public class AgendamentoResponse {
    private Long id;
    private Long motoId;
    private LocalDateTime dataAgendada;
    private String descricao;
}