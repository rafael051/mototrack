package br.com.fiap.mototrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

/**
 * 🏢 Entidade: Filial
 * Representa uma unidade da Mottu com coordenadas de geofencing.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_filial")
public class Filial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_filial")
    private Long id;

    @NotBlank
    @Column(name = "nome")
    private String nome;

    private String endereco;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;

    private Double latitude;
    private Double longitude;
    private Double raioGeofenceMetros;

    @OneToMany(mappedBy = "filial")
    private List<Moto> motos;
}