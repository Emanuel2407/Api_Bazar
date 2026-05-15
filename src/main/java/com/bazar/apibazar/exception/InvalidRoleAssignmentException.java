package com.bazar.apibazar.exception;

//Excepción personalizada de dominio para indicar que una asignación de un rol a un usuario no es válida
public class InvalidRoleAssignmentException extends RuntimeException {
    public InvalidRoleAssignmentException(String message) {
        super(message);
    }
}
