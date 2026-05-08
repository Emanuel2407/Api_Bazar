package com.bazar.apibazar.exception;

//Excepción de infraestructura de seguridad para indicar que un username ya está registrado
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
