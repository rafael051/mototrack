package br.com.fiap.mototrack.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 📦 DTO: EventoResponse
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventoResponse {

    /** Identificador único do evento */
    @Schema(example = "1", description = "ID do evento registrado")
    private Long id;

    /** ID da moto associada ao evento */
    @Schema(example = "1", description = "ID da moto vinculada ao evento")
    private Long motoId;

    /** Placa da moto associada ao evento (para UI) */
    @Schema(example = "ABC1D23", description = "Placa da moto vinculada ao evento")
    private String motoPlaca; // <-- NOVO

    /** Tipo do evento (ex: Saída, Entrada, Manutenção) */
    @Schema(example = "Entrada", description = "Tipo do evento registrado")
    private String tipo;

    /** Motivo do evento */
    @Schema(example = "Moto retornou ao pátio após entrega", description = "Motivo detalhado do evento")
    private String motivo;

    /** Data e hora do evento no formato brasileiro */
    @Schema(example = "25/05/2025 15:45", description = "Data e hora do evento (formato: dd/MM/yyyy HH:mm)")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataHora;

    /** Localização textual do evento */
    @Schema(example = "Pátio Zona Norte", description = "Local onde ocorreu o evento")
    private String localizacao;
}
