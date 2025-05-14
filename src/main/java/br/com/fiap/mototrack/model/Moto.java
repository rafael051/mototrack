package br.com.fiap.mototrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 🛵 Entidade: Moto
 *
 * Representa uma motocicleta cadastrada no sistema da Mottu.
 * Armazena dados técnicos da moto, status operacional,
 * vínculo com filial e localização geográfica.
 *
 * ---
 * @author Rafael
 * @version 1.0
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_moto")
public class Moto {

    // ===========================
    // 🔑 Identificação
    // ===========================

    /** Identificador único da moto (chave primária). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_moto")
    private Long id;

    /** Placa da moto. Deve ser única e não nula. */
    @NotBlank(message = "A placa é obrigatória.")
    @Column(name = "placa", unique = true, nullable = false)
    private String placa;

    // ===========================
    // 📋 Especificações Técnicas
    // ===========================

    /** Modelo da moto (ex: CG 160 Fan). */
    @NotBlank(message = "O modelo é obrigatório.")
    @Column(name = "modelo")
    private String modelo;

    /** Marca da moto (ex: Honda). */
    @NotBlank(message = "A marca é obrigatória.")
    @Column(name = "marca")
    private String marca;

    /** Ano de fabricação da moto. Mínimo aceito: 2000. */
    @Min(value = 2000, message = "O ano mínimo permitido é 2000.")
    @Column(name = "ano")
    private int ano;

    /** Status operacional atual da moto (ex: Disponível, Locada, Manutenção). */
    @NotBlank(message = "O status é obrigatório.")
    @Column(name = "status")
    private String status;

    // ===========================
    // 🔗 Relacionamentos
    // ===========================

    /** Filial vinculada à moto. */
    @ManyToOne
    @JoinColumn(name = "id_filial")
    private Filial filial;

    // ===========================
    // 🌐 Geolocalização
    // ===========================

    /** Latitude atual da moto. */
    @Column(name = "latitude")
    private Double latitude;

    /** Longitude atual da moto. */
    @Column(name = "longitude")
    private Double longitude;

    // ===========================
    // 🕒 Controle de criação
    // ===========================

    /** Data de criação automática da moto no sistema. */
    @Column(name = "dt_criacao", updatable = false)
    private LocalDateTime dataCriacao;
}
