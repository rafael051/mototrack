package br.com.fiap.mototrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * # 📦 HttpExceptionUtils
 *
 * Utilitário centralizado para lançar exceções HTTP (`ResponseStatusException`)
 * com mensagens padronizadas e status apropriados em toda a aplicação.
 *
 * ---
 *
 * ## 📋 Como usar?
 * Utilize este utilitário nos services, controllers ou handlers para padronizar as respostas de erro da API.
 *
 * ```java
 * throw HttpExceptionUtils.notFound("Usuario", id);
 * throw HttpExceptionUtils.badRequest("Campo obrigatório ausente");
 * throw HttpExceptionUtils.forbidden("Acesso negado ao recurso X");
 * throw HttpExceptionUtils.unauthorized("Token inválido");
 * throw HttpExceptionUtils.internalError("Erro inesperado na aplicação");
 * ```
 *
 * > **Vantagem:** Facilita manutenção, testes, centralização e internacionalização das mensagens de erro.
 *
 * ---
 *
 * @author Rafael
 * @since 1.0
 */
public final class HttpExceptionUtils {

    /**
     * Construtor privado para evitar instanciação.
     * Classe utilitária: apenas métodos estáticos.
     */
    private HttpExceptionUtils() {
        // Não instanciar
    }

    // ============================
    // 🚫 404 - NOT FOUND
    // ============================

    /**
     * Lança uma exceção HTTP 404 (`Not Found`) para uma entidade não encontrada pelo ID.
     *
     * @param entidade Nome da entidade (ex: "Usuario", "Moto", "Filial")
     * @param id Valor do identificador buscado
     * @return ResponseStatusException 404, com mensagem padronizada
     *
     * **Exemplo:**
     * ```java
     * throw HttpExceptionUtils.notFound("Moto", 123);
     * // Mensagem: "Moto não encontrada para o ID: 123"
     * ```
     */
    public static ResponseStatusException notFound(String entidade, Object id) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                String.format("%s não encontrada para o ID: %s", entidade, id)
        );
    }

    /**
     * Lança uma exceção HTTP 404 (`Not Found`) com uma mensagem customizada.
     *
     * @param mensagem Mensagem descritiva do erro
     * @return ResponseStatusException 404, com mensagem customizada
     *
     * **Exemplo:**
     * ```java
     * throw HttpExceptionUtils.notFound("Usuário inativo não pode ser consultado");
     * ```
     */
    public static ResponseStatusException notFound(String mensagem) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, mensagem);
    }

    // ============================
    // ❌ 400 - BAD REQUEST
    // ============================

    /**
     * Lança uma exceção HTTP 400 (`Bad Request`) com uma mensagem customizada.
     *
     * @param mensagem Detalhe sobre o erro de requisição
     * @return ResponseStatusException 400, com mensagem customizada
     *
     * **Exemplo:**
     * ```java
     * throw HttpExceptionUtils.badRequest("Formato do campo 'email' inválido");
     * ```
     */
    public static ResponseStatusException badRequest(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }

    // ============================
    // 🔒 403 - FORBIDDEN
    // ============================

    /**
     * Lança uma exceção HTTP 403 (`Forbidden`) quando o usuário não tem permissão para acessar o recurso.
     *
     * @param mensagem Mensagem explicando o motivo da restrição
     * @return ResponseStatusException 403, com mensagem customizada
     *
     * **Exemplo:**
     * ```java
     * throw HttpExceptionUtils.forbidden("Acesso restrito a administradores");
     * ```
     */
    public static ResponseStatusException forbidden(String mensagem) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, mensagem);
    }

    // ============================
    // 🔐 401 - UNAUTHORIZED
    // ============================

    /**
     * Lança uma exceção HTTP 401 (`Unauthorized`) para casos de autenticação inválida ou ausente.
     *
     * @param mensagem Mensagem explicando o motivo da não autorização
     * @return ResponseStatusException 401, com mensagem customizada
     *
     * **Exemplo:**
     * ```java
     * throw HttpExceptionUtils.unauthorized("Token de autenticação expirado");
     * ```
     */
    public static ResponseStatusException unauthorized(String mensagem) {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, mensagem);
    }

    // ============================
    // 💣 500 - INTERNAL SERVER ERROR
    // ============================

    /**
     * Lança uma exceção HTTP 500 (`Internal Server Error`) para erros inesperados no servidor.
     *
     * @param mensagem Detalhe técnico sobre o erro interno
     * @return ResponseStatusException 500, com mensagem customizada
     *
     * **Exemplo:**
     * ```java
     * throw HttpExceptionUtils.internalError("Erro ao processar a solicitação: NullPointerException");
     * ```
     */
    public static ResponseStatusException internalError(String mensagem) {
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, mensagem);
    }
}
