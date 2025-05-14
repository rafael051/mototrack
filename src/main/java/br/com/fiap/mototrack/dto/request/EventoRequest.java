package br.com.fiap.mototrack.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 📦 DTO: EventoRequest
 * Representa os dados necessários para registrar um evento de movimentação da moto.
 */
@Data
@NoArgsConstructor
public class EventoRequest {
    @NotNull(message = "O ID da moto é obrigatório.")
    private Long motoId;

    @NotBlank(message = "O tipo do evento é obrigatório.")
    private String tipo;

    @NotBlank(message = "O motivo do evento é obrigatório.")
    private String motivo;

    private LocalDateTime dataHora;
    private String localizacao;
}