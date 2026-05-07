package com.bazar.apibazar.exception;

//excepción personalizada por si no existe un role que se está consultando
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
