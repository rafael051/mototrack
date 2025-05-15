package br.com.fiap.mototrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 🔄 Entidade: Evento
 *
 * Representa um registro de movimentação ou alteração de status da moto,
 * como saída, manutenção ou realocação.
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
@Table(name = "tb_evento")
public class Evento {

    // ===========================
    // 🔑 Identificação
    // ===========================

    /** ID único do evento */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long id;

    // ===========================
    // 🔗 Relacionamento
    // ===========================

    /** Moto envolvida no evento */
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_moto", nullable = false)
    private Moto moto;

    // ===========================
    // 🏷️ Detalhes do Evento
    // ===========================

    /** Tipo do evento (ex: Saída, Entrada, Manutenção) */
    @NotBlank(message = "O tipo do evento é obrigatório.")
    @Column(name = "tp_evento", nullable = false)
    private String tipo;

    /** Motivo do evento */
    @NotBlank(message = "O motivo do evento é obrigatório.")
    @Column(name = "ds_motivo", nullable = false)
    private String motivo;

    /** Data e hora do evento */
    @Column(name = "dt_hr_evento", nullable = false)
    @CreationTimestamp
    private LocalDateTime dataHora;

    /** Localização textual do evento */
    @Column(name = "ds_localizacao")
    private String localizacao;
}
