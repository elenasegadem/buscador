package com.relatosPapel.buscador.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateISBNException extends RuntimeException{
    public DuplicateISBNException(String mensaje) {
        super(mensaje);
    }
}
