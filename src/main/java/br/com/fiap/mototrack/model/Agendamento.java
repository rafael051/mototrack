package br.com.fiap.mototrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 📅 Entidade: Agendamento
 *
 * Representa um agendamento de manutenção ou evento futuro para uma moto,
 * incluindo descrição e data planejada.
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
@Table(name = "tb_agendamento")
public class Agendamento {

    // ===========================
    // 🔑 Identificação
    // ===========================

    /** ID único do agendamento */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agendamento")
    private Long id;

    // ===========================
    // 🔗 Relacionamento
    // ===========================

    /** Moto relacionada ao agendamento */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_moto", nullable = false)
    private Moto moto;

    // ===========================
    // 📆 Informações do agendamento
    // ===========================

    /** Data e hora programada para o evento */
    @Column(name = "dt_agendada", nullable = false)
    private LocalDateTime dataAgendada;

    /** Descrição do agendamento */
    @NotBlank(message = "A descrição é obrigatória.")
    @Column(name = "ds_descricao", nullable = false)
    private String descricao;

    // ===========================
    // 🕒 Controle de criação (opcional)
    // ===========================

    /** Timestamp de criação do agendamento */
    @CreationTimestamp
    @Column(name = "dt_criacao", updatable = false)
    private LocalDateTime dataCriacao;
}
