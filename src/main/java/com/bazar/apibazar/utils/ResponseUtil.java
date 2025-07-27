package com.bazar.apibazar.utils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class ResponseUtil {
    
    public static Map<String, Object> notFound(Long id){
        Map<String, Object> response = new LinkedHashMap();
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 404);
        response.put("error", HttpStatus.NOT_FOUND);
        response.put("mensaje", "No se encontró registro con id: " + id);
        
        return response;
    }
    
}
