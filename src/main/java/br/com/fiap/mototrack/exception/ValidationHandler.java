package br.com.fiap.mototrack.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * # 🚨 ValidationHandler
 *
 * Captura e trata exceções de validação lançadas por `@Valid` em DTOs.
 * Retorna uma lista enxuta com os campos inválidos e suas respectivas mensagens.
 *
 * ---
 * ✅ Útil para respostas padronizadas e amigáveis no front-end.
 * Exemplo de resposta:
 * ```json
 * [
 *   { "field": "placa", "message": "A placa é obrigatória." },
 *   { "field": "ano", "message": "O ano deve ser no mínimo 2000." }
 * ]
 * ```
 *
 * @author Rafael
 * @since 1.0
 */
@RestControllerAdvice
public class ValidationHandler {

    /**
     * 📦 DTO interno para representar erros de validação.
     *
     * - `field`: nome do campo inválido
     * - `message`: mensagem associada à falha de validação
     */
    record ValidationError(String field, String message) {
        public ValidationError(FieldError fieldError) {
            this(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }

    /**
     * ⚠️ Intercepta `MethodArgumentNotValidException` (erros de validação em @RequestBody).
     *
     * @param ex exceção capturada
     * @return lista de erros formatados
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationError> handle(MethodArgumentNotValidException ex) {
        return ex.getFieldErrors()
                .stream()
                .map(ValidationError::new)
                .toList();
    }
}
