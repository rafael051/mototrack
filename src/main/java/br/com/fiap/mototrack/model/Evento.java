package br.com.fiap.mototrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 🔄 Entidade: Evento
 * Representa um registro de movimentação de uma moto.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Moto moto;

    @NotBlank
    private String tipo;

    @NotBlank
    private String motivo;

    private LocalDateTime dataHora;

    private String localizacao;
}