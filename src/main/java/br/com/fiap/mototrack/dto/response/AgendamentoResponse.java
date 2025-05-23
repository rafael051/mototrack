package br.com.fiap.mototrack.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 📦 DTO: AgendamentoResponse
 *
 * Representa os dados retornados pela API para um agendamento realizado no sistema.
 * Fornece informações sobre a moto vinculada, data agendada e descrição do evento.
 *
 * ---
 * @author Rafael
 * @version 1.0
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgendamentoResponse {

    /** Identificador único do agendamento */
    @Schema(example = "1001", description = "ID do agendamento")
    private Long id;

    /** Identificador da moto associada ao agendamento */
    @Schema(example = "5", description = "ID da moto vinculada ao agendamento")
    private Long motoId;

    /** Data e hora do agendamento no formato brasileiro */
    @Schema(
            example = "25/05/2025 14:00",
            description = "Data e hora agendada (formato: dd/MM/yyyy HH:mm)"
    )
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataAgendada;


    /** Descrição do motivo do agendamento */
    @Schema(example = "Manutenção preventiva programada", description = "Descrição do agendamento")
    private String descricao;
}
