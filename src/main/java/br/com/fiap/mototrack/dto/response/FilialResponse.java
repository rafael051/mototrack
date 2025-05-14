package br.com.fiap.mototrack.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 DTO: FilialResponse
 * Representa os dados retornados da API relacionados a uma Filial.
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilialResponse {
    private Long id;
    private String nome;
    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private Double latitude;
    private Double longitude;
    private Double raioGeofenceMetros;
}
