package br.com.fiap.mototrack.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 DTO: UsuarioRequest
 * Representa os dados recebidos para registrar um novo usuário do sistema.
 */
@Data
@NoArgsConstructor
public class UsuarioRequest {
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @Email(message = "Email inválido.")
    @NotBlank(message = "O email é obrigatório.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    private String senha;

    @NotBlank(message = "O perfil é obrigatório.")
    private String perfil;
}
