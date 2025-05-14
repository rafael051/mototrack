package br.com.fiap.mototrack.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 📦 DTO: AgendamentoRequest
 * Representa os dados para agendar um evento ou manutenção de uma moto.
 */
@Data
@NoArgsConstructor
public class AgendamentoRequest {
    @NotNull(message = "O ID da moto é obrigatório.")
    private Long motoId;

    @FutureOrPresent(message = "A data agendada não pode estar no passado.")
    private LocalDateTime dataAgendada;

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;
}
