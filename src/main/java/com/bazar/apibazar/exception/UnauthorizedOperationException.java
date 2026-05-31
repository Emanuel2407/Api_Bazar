package com.bazar.apibazar.exception;

/**
 * Excepción para indicar que el usuario autenticado no puede realizar una cierta operación.
 */
public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
