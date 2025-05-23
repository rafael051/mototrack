package br.com.fiap.mototrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 DTO: UsuarioRequest
 *
 * Representa os dados recebidos para registrar um novo usuário no sistema.
 * Inclui validações de entrada e documentação Swagger/OpenAPI.
 *
 * ---
 * @author Rafael
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class UsuarioRequest {

    // ===========================
    // 🙍 Dados Pessoais
    // ===========================

    /**
     * Nome completo do usuário.
     */
    @Schema(example = "João da Silva", description = "Nome completo do usuário")
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    /**
     * Email de login do usuário.
     */
    @Schema(example = "joao.silva@example.com", description = "Endereço de email do usuário")
    @Email(message = "Email inválido.")
    @NotBlank(message = "O email é obrigatório.")
    private String email;

    /**
     * Senha para autenticação.
     */
    @Schema(example = "SenhaForte123!", description = "Senha de acesso do usuário")
    @NotBlank(message = "A senha é obrigatória.")
    private String senha;

    /**
     * Perfil de acesso (ex: ADMIN, USUARIO).
     */
    @Schema(example = "ADMIN", description = "Perfil do usuário no sistema (ex: ADMIN, USUARIO)")
    @NotBlank(message = "O perfil é obrigatório.")
    private String perfil;

    // ===========================
    // 🔗 Relacionamento
    // ===========================

    /**
     * Código da filial vinculada ao usuário.
     * Opcional: se não informado, usuário não estará vinculado a nenhuma filial.
     */
    @Schema(example = "1", description = "ID da filial à qual o usuário pertence")
    private Long filialId;
}
