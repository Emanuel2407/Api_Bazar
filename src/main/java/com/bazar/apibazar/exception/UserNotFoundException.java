package com.bazar.apibazar.exception;

//Excepción personalizada que indica que un usuario consultado no existe
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
