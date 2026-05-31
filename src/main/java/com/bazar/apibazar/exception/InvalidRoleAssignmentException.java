package com.bazar.apibazar.exception;

/**
 * Indica que la asignación de un rol a un usuario no es válida
 */
public class InvalidRoleAssignmentException extends RuntimeException {
    public InvalidRoleAssignmentException(String message) {
        super(message);
    }
}
