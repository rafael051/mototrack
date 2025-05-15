package br.com.fiap.mototrack.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * # 🚨 GlobalExceptionHandler
 *
 * Classe responsável por capturar, tratar e responder a todas as exceções lançadas pela aplicação.
 * Retorna sempre objetos JSON padronizados de erro, no formato {@link ErrorResponse}.
 *
 * ---
 *
 * ## Por que usar?
 * - Centraliza o tratamento de erros, reduz repetição de código.
 * - Garante mensagens consistentes e padronizadas ao consumidor da API.
 * - Facilita troubleshooting e integração front-end/back-end.
 *
 * ---
 *
 * ## Exemplo de resposta JSON padrão:
 * ```json
 * {
 *   "timestamp": "2025-05-15T17:00:00",
 *   "status": 400,
 *   "message": "Erro de validação",
 *   "fieldErrors": [
 *     { "field": "email", "message": "O campo email é obrigatório" }
 *   ]
 * }
 * ```
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ---
     * ## 🧪 Erros de validação no corpo da requisição (`@Valid`)
     *
     * Captura falhas de validação ao receber DTOs no corpo da requisição.
     * Normalmente disparado quando há anotações como `@NotNull`, `@Email`, `@Size`, etc.
     *
     * **Exemplo de cenário:**
     * Enviar um JSON com o campo "email" vazio ou inválido em um POST.
     *
     * **Status retornado:** 400 BAD REQUEST
     *
     * **Exemplo de resposta:**
     * ```json
     * {
     *   "timestamp": "...",
     *   "status": 400,
     *   "message": "Erro de validação",
     *   "fieldErrors": [
     *     { "field": "email", "message": "O campo email é obrigatório" }
     *   ]
     * }
     * ```
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ErrorResponse.FieldError(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.toList());

        log.warn("Erro de validação: {}", errors);

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Erro de validação", errors));
    }

    /**
     * ---
     * ## ❌ ResponseStatusException
     *
     * Captura exceções lançadas manualmente usando {@link org.springframework.web.server.ResponseStatusException},
     * incluindo os utilitários de notFound, badRequest, forbidden, etc.
     *
     * **Exemplo de cenário:**
     * Buscar uma entidade que não existe, ou tentar acessar recurso sem permissão.
     *
     * **Status retornado:** conforme definido no lançamento da exceção
     *
     * **Exemplo de resposta:**
     * ```json
     * {
     *   "timestamp": "...",
     *   "status": 404,
     *   "message": "Usuario não encontrado para o ID: 10"
     * }
     * ```
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        log.warn("Exceção manual: {}", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ErrorResponse(ex.getStatusCode().value(), ex.getReason()));
    }

    /**
     * ---
     * ## ⚠️ Violação de constraints em parâmetros (`@RequestParam`, `@PathVariable`)
     *
     * Captura violações de restrições em parâmetros de rota ou query (ex: valor fora do padrão, tipo errado).
     *
     * **Exemplo de cenário:**
     * Receber um valor negativo onde só é permitido positivo, ou string em vez de número.
     *
     * **Status retornado:** 400 BAD REQUEST
     *
     * **Exemplo de resposta:**
     * ```json
     * {
     *   "timestamp": "...",
     *   "status": 400,
     *   "message": "Violação de restrição: must be greater than 0"
     * }
     * ```
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Violação de restrição: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Violação de restrição: " + ex.getMessage()));
    }

    /**
     * ---
     * ## 💣 Erro inesperado (fallback)
     *
     * Captura qualquer outra exceção não tratada explicitamente pelos métodos anteriores.
     * É um "catch-all" para evitar que detalhes sensíveis do backend sejam expostos.
     *
     * **Status retornado:** 500 INTERNAL SERVER ERROR
     *
     * **Exemplo de resposta:**
     * ```json
     * {
     *   "timestamp": "...",
     *   "status": 500,
     *   "message": "NullPointerException"
     * }
     * ```
     *
     * > **Dica:** Sempre monitore os logs em produção para identificar possíveis falhas recorrentes.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        log.error("Erro inesperado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }
}
