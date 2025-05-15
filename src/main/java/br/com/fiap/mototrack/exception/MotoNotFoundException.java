package br.com.fiap.mototrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * ❌ Exceção para Moto não encontrada.
 */
public class MotoNotFoundException extends ResponseStatusException {
    public MotoNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Moto não encontrada");
    }

    public MotoNotFoundException(String msg) {
        super(HttpStatus.NOT_FOUND, msg);
    }
}
