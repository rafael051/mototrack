package br.com.fiap.mototrack.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 DTO: FilialRequest
 * Representa os dados recebidos para criação ou atualização de uma Filial.
 */
@Data
@NoArgsConstructor
public class FilialRequest {

    @NotBlank(message = "O nome da filial é obrigatório.")
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
