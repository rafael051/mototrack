package br.com.fiap.mototrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * ❌ Exceção para Filial não encontrada.
 */
public class FilialNotFoundException extends ResponseStatusException {
    public FilialNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Filial não encontrada");
    }
}
