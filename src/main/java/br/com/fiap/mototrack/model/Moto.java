package br.com.fiap.mototrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 🛵 Entidade: Moto
 *
 * Representa uma motocicleta cadastrada no sistema da Mottu.
 * Armazena dados técnicos, status operacional, vínculo com filial e localização.
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

    /** ID único da moto */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_moto")
    private Long id;

    /** Placa da moto (única) */
    @NotBlank(message = "A placa é obrigatória.")
    @Column(name = "cd_placa", unique = true, nullable = false)
    private String placa;

    // ===========================
    // 📋 Especificações
    // ===========================

    /** Modelo da moto */
    @NotBlank(message = "O modelo é obrigatório.")
    @Column(name = "ds_modelo")
    private String modelo;

    /** Marca da moto */
    @NotBlank(message = "A marca é obrigatória.")
    @Column(name = "ds_marca")
    private String marca;

    /** Ano de fabricação */
    @Min(value = 2000, message = "O ano mínimo permitido é 2000.")
    @Column(name = "nr_ano")
    private int ano;

    /** Status da moto (ex: Disponível, Locada) */
    @NotBlank(message = "O status é obrigatório.")
    @Column(name = "ds_status")
    private String status;

    // ===========================
    // 🔗 Relacionamento
    // ===========================

    /** Filial associada à moto */
    @ManyToOne
    @JoinColumn(name = "id_filial")
    private Filial filial;

    // ===========================
    // 🌐 Localização
    // ===========================

    /** Latitude atual */
    @Column(name = "vl_latitude")
    private Double latitude;

    /** Longitude atual */
    @Column(name = "vl_longitude")
    private Double longitude;

    // ===========================
    // 🕒 Controle
    // ===========================

    /** Data/hora de criação do registro */
    @CreationTimestamp
    @Column(name = "dt_criacao", updatable = false)
    private LocalDateTime dataCriacao;
}
