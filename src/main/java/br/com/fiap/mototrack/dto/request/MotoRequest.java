package br.com.fiap.mototrack.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 DTO: MotoRequest
 *
 * Representa os dados recebidos para criação ou atualização de uma Moto no sistema.
 * Inclui validações e controle de entrada via Bean Validation.
 *
 * ---
 * @author Rafael
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class MotoRequest {

    // ===========================
    // 🔑 Identificadores
    // ===========================

    /**
     * Placa da moto. Deve ser única e não vazia.
     */
    @Schema(example = "ABC1234", description = "Placa da moto (única)")
    @NotBlank(message = "A placa é obrigatória.")
    private String placa;

    /**
     * Modelo da moto (ex: CG 160).
     */
    @Schema(example = "CG 160", description = "Modelo da moto")
    @NotBlank(message = "O modelo é obrigatório.")
    private String modelo;

    /**
     * Marca da moto (ex: Honda).
     */
    @Schema(example = "Honda", description = "Marca da moto")
    @NotBlank(message = "A marca é obrigatória.")
    private String marca;

    /**
     * Ano de fabricação da moto (mínimo 2000).
     */
    @Schema(example = "2022", description = "Ano de fabricação (mínimo 2000)")
    @Min(value = 2000, message = "O ano deve ser no mínimo 2000.")
    private int ano;

    /**
     * Status atual da moto (ex: Disponível, Locada).
     */
    @Schema(example = "Disponível", description = "Status atual da moto")
    @NotBlank(message = "O status é obrigatório.")
    private String status;

    // ===========================
    // 🔗 Relacionamentos
    // ===========================

    /**
     * Identificador da filial a qual a moto pertence.
     */
    @Schema(example = "1", description = "ID da filial vinculada")
    private Long filialId;

    // ===========================
    // 🌐 Localização GPS
    // ===========================

    /**
     * Latitude atual da moto.
     */
    @Schema(example = "-23.567890", description = "Latitude da moto")
    private Double latitude;

    /**
     * Longitude atual da moto.
     */
    @Schema(example = "-46.654321", description = "Longitude da moto")
    private Double longitude;
}
