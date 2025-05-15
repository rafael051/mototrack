package br.com.fiap.mototrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

/**
 * 🏢 Entidade: Filial
 *
 * Representa uma unidade da Mottu com informações de localização, endereço e raio de geofencing.
 * Utilizada como ponto de referência para alocar motos e controlar suas localizações.
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
@Table(name = "tb_filial")
public class Filial {


    // ===========================
    // 🔑 Identificação
    // ===========================

    /**
     * Identificador único da filial (chave primária).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_filial")
    private Long id;

    // ===========================
    // 🏷️ Dados da Filial
    // ===========================

    /**
     * Nome da filial (ex: Pátio Lapa).
     */
    @NotBlank(message = "O nome da filial é obrigatório.")
    @Column(name = "nm_filial", nullable = false)
    private String nome;

    /**
     * Endereço completo (rua e número).
     */
    @Column(name = "ds_endereco")
    private String endereco;

    /**
     * Bairro da filial.
     */
    @Column(name = "ds_bairro")
    private String bairro;

    /**
     * Cidade onde a filial está localizada.
     */
    @Column(name = "ds_cidade")
    private String cidade;

    /**
     * Estado da filial.
     */
    @Column(name = "ds_estado")
    private String estado;

    /**
     * Código postal (CEP).
     */
    @Column(name = "nr_cep")
    private String cep;

    // ===========================
    // 🌐 Geolocalização
    // ===========================

    /**
     * Coordenada de latitude (ex: -23.564312).
     */
    @Column(name = "vl_latitude")
    private Double latitude;

    /**
     * Coordenada de longitude (ex: -46.654212).
     */
    @Column(name = "vl_longitude")
    private Double longitude;

    /**
     * Raio de geofencing em metros (ex: 100.0).
     */
    @Column(name = "raio_geofence_m")
    private Double raioGeofenceMetros;

    // ===========================
    // 🔗 Relacionamentos
    // ===========================

    /**
     * Lista de motos alocadas nesta filial.
     */
    @OneToMany(mappedBy = "filial")
    private List<Moto> motos;
}
