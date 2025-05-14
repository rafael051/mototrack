package br.com.fiap.mototrack.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 🛵 Entidade: Moto
 * Representa uma motocicleta registrada no sistema da Mottu.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_moto")
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_moto")
    private Long id;

    @NotBlank
    @Column(name = "placa", unique = true, nullable = false)
    private String placa;

    @NotBlank
    @Column(name = "modelo")
    private String modelo;

    @NotBlank
    @Column(name = "marca")
    private String marca;

    @Min(2000)
    @Column(name = "ano")
    private int ano;

    @NotBlank
    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_filial")
    private Filial filial;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "dt_criacao", updatable = false)
    private LocalDateTime dataCriacao;
}