package br.com.fiap.mototrack.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 👤 Entidade: Usuario
 *
 * Representa um usuário do sistema com credenciais de acesso e perfil funcional.
 * Pode ter papel de OPERADOR, GESTOR ou ADMINISTRADOR.
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
@Table(name = "tb_usuario")
public class Usuario {

    // ===========================
    // 🔑 Identificação
    // ===========================

    /**
     * Identificador único do usuário.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    // ===========================
    // 🧑 Dados pessoais
    // ===========================

    /**
     * Nome completo do usuário.
     */
    @NotBlank(message = "O nome é obrigatório.")
    @Column(name = "nm_usuario", nullable = false)
    private String nome;

    /**
     * Email do usuário, usado para login. Deve ser único.
     */
    @Email(message = "Email inválido.")
    @NotBlank(message = "O email é obrigatório.")
    @Column(name = "ds_email", nullable = false, unique = true)
    private String email;

    /**
     * Senha de acesso ao sistema. Deve ser armazenada criptografada.
     */
    @NotBlank(message = "A senha é obrigatória.")
    @Column(name = "ds_senha", nullable = false)
    private String senha;

    /**
     * Perfil do usuário (ex: OPERADOR, GESTOR, ADMINISTRADOR).
     */
    @NotBlank(message = "O perfil é obrigatório.")
    @Column(name = "tp_perfil", nullable = false)
    private String perfil;

    // ===========================
    // 🔗 Relacionamento
    // ===========================

    /**
     * Filial vinculada ao usuário.
     * Representa a FK ID_FILIAL no banco.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_filial") // nome exato da coluna FK
    private Filial filial;

}
