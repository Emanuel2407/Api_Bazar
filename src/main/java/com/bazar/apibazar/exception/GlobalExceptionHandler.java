package com.bazar.apibazar.exception;

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
}
