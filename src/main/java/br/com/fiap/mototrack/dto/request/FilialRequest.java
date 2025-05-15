package br.com.fiap.mototrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 DTO: FilialRequest
 *
 * Representa os dados recebidos para criação ou atualização de uma Filial no sistema.
 * Inclui validações e documentação Swagger para uso com OpenAPI.
 *
 * ---
 * @author Rafael
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class FilialRequest {

    // ===========================
    // 🏢 Dados da Filial
    // ===========================

    /**
     * Nome da filial.
     */
    @Schema(example = "Filial Lapa", description = "Nome da filial")
    @NotBlank(message = "O nome da filial é obrigatório.")
    private String nome;

    /**
     * Endereço da filial.
     */
    @Schema(example = "Rua Clélia, 1000", description = "Endereço completo da filial")
    private String endereco;

    @Schema(example = "Lapa", description = "Bairro onde a filial está localizada")
    private String bairro;

    @Schema(example = "São Paulo", description = "Cidade da filial")
    private String cidade;

    @Schema(example = "SP", description = "Estado da filial (UF)")
    private String estado;

    @Schema(example = "05042-000", description = "CEP da filial")
    private String cep;

    // ===========================
    // 📍 Localização GPS
    // ===========================

    /**
     * Latitude da filial.
     */
    @Schema(example = "-23.530123", description = "Latitude da localização da filial")
    private Double latitude;

    /**
     * Longitude da filial.
     */
    @Schema(example = "-46.678456", description = "Longitude da localização da filial")
    private Double longitude;

    /**
     * Raio de geofência da filial em metros.
     */
    @Schema(example = "100.0", description = "Raio de atuação da filial em metros para geofencing")
    private Double raioGeofenceMetros;
}
