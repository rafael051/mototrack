package br.com.fiap.mototrack.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 DTO: UsuarioResponse
 * Representa os dados do usuário retornados nas respostas da API.
 */
@Data
@NoArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private String perfil;
}