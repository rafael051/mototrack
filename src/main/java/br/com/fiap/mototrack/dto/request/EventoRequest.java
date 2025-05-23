package br.com.fiap.mototrack.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 📦 DTO: EventoRequest
 *
 * Representa os dados necessários para registrar um evento de movimentação da moto.
 * Inclui validações e documentação Swagger.
 *
 * ---
 * @author Rafael
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class EventoRequest {

    // ===========================
    // 🔗 Identificador da Moto
    // ===========================

    /**
     * ID da moto envolvida no evento.
     */
    @Schema(example = "1", description = "ID da moto que sofreu o evento")
    @NotNull(message = "O ID da moto é obrigatório.")
    private Long motoId;

    // ===========================
    // 🏷️ Tipo e Motivo
    // ===========================

    /**
     * Tipo do evento (ex: Saída, Entrada, Manutenção).
     */
    @Schema(example = "Saída", description = "Tipo do evento registrado")
    @NotBlank(message = "O tipo do evento é obrigatório.")
    private String tipo;

    /**
     * Motivo do evento (ex: Entrega agendada).
     */
    @Schema(example = "Entrega programada para zona sul", description = "Motivo do evento")
    @NotBlank(message = "O motivo do evento é obrigatório.")
    private String motivo;

    // ===========================
    // 📆 Data e Local
    // ===========================

    /**
     * Data e hora do evento no formato brasileiro.
     */
    @Schema(
            example = "01/06/2025 14:00:00",
            description = "Data e hora do agendamento no formato dd/MM/yyyy HH:mm:ss (não pode ser passada)"
    )
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @FutureOrPresent(message = "A data agendada não pode estar no passado.")
    private LocalDateTime dataHora;

    /**
     * Localização textual do evento (ex: nome do pátio ou endereço).
     */
    @Schema(example = "Pátio Lapa - São Paulo", description = "Localização aproximada do evento")
    private String localizacao;
}
