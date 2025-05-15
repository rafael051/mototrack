package br.com.fiap.mototrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * 🚫 Exceção: OrdenacaoInvalidaException
 *
 * Lançada quando o campo de ordenação enviado via query param não é suportado
 * pela aplicação. Evita falhas de SQL causadas por colunas inexistentes.
 *
 * ---
 * Exemplo de uso:
 * if (!List.of("placa", "modelo").contains(sortField)) {
 *     throw new OrdenacaoInvalidaException("Campo de ordenação inválido: '" + sortField + "'");
 * }
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public class OrdenacaoInvalidaException extends ResponseStatusException {

    public OrdenacaoInvalidaException(String campo) {
        super(HttpStatus.BAD_REQUEST, "Campo de ordenação inválido: '" + campo + "'");
    }
}
