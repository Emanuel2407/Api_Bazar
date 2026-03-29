package com.bazar.apibazar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

//Definimos manejador global de excepciones para manejar cada error de dominio que pueda surgir
@RestControllerAdvice
public class GlobalExceptionHandler {

    //Método propio para construir la respone de error de cada excepción
    private Map<String, Object> buildExceptionResponse(HttpStatus status, String message){
        Map<String, Object> response = new LinkedHashMap();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("mensaje", message);

        return response;
    }
}
