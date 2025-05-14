package br.com.fiap.mototrack.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private Long id;

    /** Placa da moto */
    private String placa;

    /** Modelo da moto (ex: CG 160) */
    private String modelo;

    /** Marca da moto (ex: Honda) */
    private String marca;

    /** Ano de fabricação */
    private int ano;

    /** Status atual (ex: Disponível, Locada) */
    private String status;

    /** ID da filial vinculada */
    private Long filialId;

    /** Latitude atual */
    private Double latitude;

    /** Longitude atual */
    private Double longitude;
}
