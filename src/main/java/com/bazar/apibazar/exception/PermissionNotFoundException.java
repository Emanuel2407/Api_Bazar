package com.bazar.apibazar.exception;

//Excepción personalizada para cuando no se encuentré un permiso solicitado
public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException(String message) {
        super(message);
    }
}
