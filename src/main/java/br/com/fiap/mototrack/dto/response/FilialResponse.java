package br.com.fiap.mototrack.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 DTO: FilialResponse
 *
 * Representa os dados retornados da API relacionados a uma Filial cadastrada no sistema.
 * Inclui informações geográficas e dados administrativos da unidade.
 *
 * ---
 * @author Rafael
 * @version 1.0
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilialResponse {

    /** Identificador único da filial */
    @Schema(example = "1", description = "ID único da filial")
    private Long id;

    /** Nome da filial */
    @Schema(example = "Filial Mooca", description = "Nome da filial")
    private String nome;

    /** Endereço da filial */
    @Schema(example = "Rua dos Trilhos, 123", description = "Endereço da filial")
    private String endereco;

    /** Bairro da filial */
    @Schema(example = "Mooca", description = "Bairro onde a filial está localizada")
    private String bairro;

    /** Cidade da filial */
    @Schema(example = "São Paulo", description = "Cidade da filial")
    private String cidade;

    /** Estado da filial */
    @Schema(example = "SP", description = "Estado da filial")
    private String estado;

    /** CEP da filial */
    @Schema(example = "03167-090", description = "CEP da filial")
    private String cep;

    /** Latitude da filial */
    @Schema(example = "-23.563812", description = "Coordenada geográfica - latitude")
    private Double latitude;

    /** Longitude da filial */
    @Schema(example = "-46.635491", description = "Coordenada geográfica - longitude")
    private Double longitude;

    /** Raio da cerca geográfica em metros */
    @Schema(example = "150.0", description = "Raio da geofence em metros")
    private Double raioGeofenceMetros;
}
