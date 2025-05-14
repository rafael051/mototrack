package br.com.fiap.mototrack.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 DTO: MotoResponse
 *
 * Representa os dados retornados da API após a criação ou consulta de uma Moto.
 * Utilizado para encapsular os dados de saída sem expor a entidade diretamente.
 *
 * ---
 * Inclui informações de identificação, especificações da moto, status operacional
 * e vínculo com filial, além de geolocalização.
 *
 * @author Rafael
 * @version 1.0
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MotoResponse {

    /** Identificador único da moto */
    @Schema(example = "1", description = "Identificador único da moto")
    private Long id;

    /** Placa da moto */
    @Schema(example = "ABC1234", description = "Placa da moto cadastrada")
    private String placa;

    /** Modelo da moto (ex: CG 160) */
    @Schema(example = "CG 160", description = "Modelo da motocicleta")
    private String modelo;

    /** Marca da moto (ex: Honda) */
    @Schema(example = "Honda", description = "Fabricante da motocicleta")
    private String marca;

    /** Ano de fabricação */
    @Schema(example = "2023", description = "Ano de fabricação")
    private int ano;

    /** Status atual (ex: Disponível, Locada) */
    @Schema(example = "Disponível", description = "Status operacional da moto")
    private String status;

    /** ID da filial vinculada */
    @Schema(example = "2", description = "Filial onde a moto está alocada")
    private Long filialId;

    /** Latitude atual */
    @Schema(example = "-23.564312", description = "Latitude atual da moto")
    private Double latitude;

    /** Longitude atual */
    @Schema(example = "-46.654212", description = "Longitude atual da moto")
    private Double longitude;
}
