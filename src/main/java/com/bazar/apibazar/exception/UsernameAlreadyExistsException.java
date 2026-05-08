package com.bazar.apibazar.exception;

//Excepción de infraestructura de seguridad para indicar que un username ya está registrado
public class UsernameIAlreadyExistsException extends RuntimeException {
    public UsernameIAlreadyExistsException(String message) {
        super(message);
    }
}
