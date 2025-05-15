package br.com.fiap.mototrack.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 DTO: UsuarioResponse
 *
 * Representa os dados do usuário retornados nas respostas da API.
 * Inclui informações básicas de identificação, e-mail e perfil de acesso.
 *
 * ---
 * @author Rafael
 * @version 1.0
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioResponse {

    /** Identificador único do usuário */
    @Schema(example = "1", description = "ID único do usuário")
    private Long id;

    /** Nome completo do usuário */
    @Schema(example = "Rafael Silva", description = "Nome completo do usuário")
    private String nome;

    /** E-mail do usuário */
    @Schema(example = "rafael.silva@fiap.com.br", description = "E-mail do usuário")
    private String email;

    /** Perfil do usuário (ex: ADMIN, USER) */
    @Schema(example = "ADMIN", description = "Perfil de acesso do usuário")
    private String perfil;
}
