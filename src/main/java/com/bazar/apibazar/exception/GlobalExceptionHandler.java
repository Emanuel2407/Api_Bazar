package com.bazar.apibazar.exception;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

//Definimos manejador global de excepciones para manejar cada error de dominio por medio de un Handler
@RestControllerAdvice
public class GlobalExceptionHandler {

    //Método propio para construir la respone de error de cada excepción
    private Map<String, Object> buildExceptionResponse(HttpStatus status, String message){
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("mensaje", message);

        return response;
    }

    //Handler para excepción ClienteNotFound
    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerClienteNotFound(ClienteNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    //Handler para excepción ProductoNotFound
    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerProductoNotFound(ProductoNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    //Handler para excepción ProductoStockInsuficiente
    @ExceptionHandler(ProductoStockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handlerProductoStockInsuficiente(ProductoStockInsuficienteException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildExceptionResponse(HttpStatus.CONFLICT, ex.getMessage()));
    }

    //Handler para excepción VentaNotFound
    @ExceptionHandler(VentaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerVentaNotFound(VentaNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    //Handler para excepción UserNotFound
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerUserNotFound(UserNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    //Handler para excepción RoleNotFound
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerRoleNotFound(RoleNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    //Handler para excepción PermissionNotFound
    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlerPermissionNotFound(PermissionNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildExceptionResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    //Handler para excepción UsernameAlreadyExists
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handlerUsernameIAlreadyExists(UsernameAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildExceptionResponse(HttpStatus.CONFLICT, ex.getMessage()));
    }

    //Handler para excepción InvalidRoleAssignment
    @ExceptionHandler(InvalidRoleAssignmentException.class)
    public ResponseEntity<Map<String, Object>> handlerInvalidRoleAssignment(InvalidRoleAssignmentException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildExceptionResponse(HttpStatus.CONFLICT, ex.getMessage()));
    }

    //Handler para excepción VentaCanceled
    @ExceptionHandler(VentaCanceledException.class)
    public ResponseEntity<Map<String, Object>> handlerVentaCanceled(VentaCanceledException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        buildExceptionResponse(HttpStatus.CONFLICT, ex.getMessage())
                );
    }

    //Handler para excepción UnauthorizedOperation
    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<Map<String, Object>> handlerUnauthorizedOperation(UnauthorizedOperationException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildExceptionResponse(HttpStatus.FORBIDDEN, ex.getMessage())
                );
    }
}
