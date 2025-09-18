package br.com.gubee.interview.core.exception;

import java.io.Serializable;
import java.time.Instant;

// Um DTO (Data Transfer Object) simples para padronizar as respostas de erro da sua API.
// Usar um record é uma forma concisa e moderna de o fazer.
public record StandardError(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path
) implements Serializable {
    private static final long serialVersionUID = 1L;
}