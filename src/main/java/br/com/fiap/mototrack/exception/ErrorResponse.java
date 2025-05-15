package br.com.fiap.mototrack.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * # ✅ ErrorResponse
 *
 * Estrutura **padrão** para respostas de erro retornadas pela API REST.
 *
 * Esta classe é utilizada pelo handler global de exceções para retornar erros
 * em formato consistente e amigável ao consumidor da API (frontend, Postman, etc).
 *
 * ---
 *
 * ## Estrutura do JSON retornado
 * ```json
 * {
 *   "timestamp": "2025-05-15T16:42:00.000",
 *   "status": 400,
 *   "message": "Campos obrigatórios não informados",
 *   "fieldErrors": [
 *     {
 *       "field": "email",
 *       "message": "O campo email é obrigatório"
 *     },
 *     {
 *       "field": "senha",
 *       "message": "A senha deve ter pelo menos 8 caracteres"
 *     }
 *   ]
 * }
 * ```
 * - O campo `fieldErrors` só aparece quando houver erro de validação em campos específicos.
 *
 * ---
 *
 * ## Como usar
 * - Retorne uma instância desta classe no seu `@RestControllerAdvice` sempre que tratar erros da API.
 * - Pode ser utilizada tanto para erros simples (ex: not found, bad request) quanto para erros de validação em massa.
 *
 * ---
 *
 * @param timestamp    Data e hora do erro (gerado automaticamente)
 * @param status       Código HTTP do erro (ex: 404, 400, 500)
 * @param message      Mensagem principal do erro (amigável ao usuário ou dev)
 * @param fieldErrors  Lista detalhada de erros de campo, para erros de validação
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        List<FieldError> fieldErrors // apenas usado quando houver múltiplos erros de validação
) {
    /**
     * Construtor prático para erros simples, sem detalhes de campos.
     *
     * @param status   Código HTTP do erro
     * @param message  Mensagem amigável
     *
     * **Exemplo:**
     * ```java
     * new ErrorResponse(404, "Recurso não encontrado");
     * ```
     */
    public ErrorResponse(int status, String message) {
        this(LocalDateTime.now(), status, message, null);
    }

    /**
     * Construtor para erros de validação com múltiplos campos inválidos.
     *
     * @param status       Código HTTP
     * @param message      Mensagem principal
     * @param fieldErrors  Lista de campos com erro
     *
     * **Exemplo:**
     * ```java
     * List<ErrorResponse.FieldError> erros = List.of(
     *     new ErrorResponse.FieldError("email", "O campo email é obrigatório"),
     *     new ErrorResponse.FieldError("senha", "A senha deve ter pelo menos 8 caracteres")
     * );
     * new ErrorResponse(400, "Erros de validação", erros);
     * ```
     */
    public ErrorResponse(int status, String message, List<FieldError> fieldErrors) {
        this(LocalDateTime.now(), status, message, fieldErrors);
    }

    /**
     * ## 📄 FieldError
     *
     * Representa um erro de validação em campo específico do objeto.
     *
     * - **field:** nome do campo com erro (ex: "email", "senha")
     * - **message:** mensagem descritiva do erro
     *
     * **Exemplo JSON:**
     * ```json
     * {
     *   "field": "senha",
     *   "message": "A senha deve ter pelo menos 8 caracteres"
     * }
     * ```
     */
    public static record FieldError(String field, String message) {}
}
