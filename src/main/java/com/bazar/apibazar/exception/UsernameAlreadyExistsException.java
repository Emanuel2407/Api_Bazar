package com.bazar.apibazar.exception;

/**
 * Excepción de infraestructura de seguridad para indicar
 * que un nombre de usuario ya está registrado
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
