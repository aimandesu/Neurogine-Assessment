package org.example.namia.supportdeskspringboot.exception;

import org.example.namia.supportdeskspringboot.model.shared.Failure;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Failure<Void> handleNotFound(ResourceNotFoundException ex) //or use ?
    {
        return new Failure<>(
                ex.getMessage(),
                ex.getDescription(),
                ex.getCustomObject()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Failure<?> handleIllegalState(IllegalStateException ex)
    {
        return new Failure<>(
                ex.getMessage(),
                "State Conflict",
                null
        );
    }
}
