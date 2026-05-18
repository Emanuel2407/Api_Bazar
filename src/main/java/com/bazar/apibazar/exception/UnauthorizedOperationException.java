package com.bazar.apibazar.exception;

//Excepción personalizada de infraestructura de seguridad para indicar que un usuario autenticado no puede realizar una cierta operación
public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
