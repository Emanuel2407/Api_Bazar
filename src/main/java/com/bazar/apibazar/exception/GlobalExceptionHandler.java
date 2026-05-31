package com.bazar.apibazar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centraliza el manejo de errores conocidos
 *  y devuelve una respuesta de error personalizada.
 * */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Construye el cuerpo de la respuesta de error.
     */
    private Map<String, Object> buildExceptionResponse(HttpStatus status, String message){
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("mensaje", message);

        return response;
    }

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerClienteNotFound(ClienteNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerProductoNotFound(ProductoNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ProductoStockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handlerProductoStockInsuficiente(ProductoStockInsuficienteException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildExceptionResponse(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(VentaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerVentaNotFound(VentaNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerUserNotFound(UserNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerRoleNotFound(RoleNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerPermissionNotFound(PermissionNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handlerUsernameIAlreadyExists(UsernameAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildExceptionResponse(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(InvalidRoleAssignmentException.class)
    public ResponseEntity<Map<String, Object>> handlerInvalidRoleAssignment(InvalidRoleAssignmentException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildExceptionResponse(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(VentaCanceledException.class)
    public ResponseEntity<Map<String, Object>> handlerVentaCanceled(VentaCanceledException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        buildExceptionResponse(HttpStatus.CONFLICT, ex.getMessage())
                );
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<Map<String, Object>> handlerUnauthorizedOperation(UnauthorizedOperationException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildExceptionResponse(HttpStatus.FORBIDDEN, ex.getMessage())
                );
    }
}
