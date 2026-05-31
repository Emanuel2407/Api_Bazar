package com.bazar.apibazar.dto.cliente;


public record ClienteResponseDto(Long idCliente,
                                 String nombre,
                                 String apellido,
                                 String documento,
                                 boolean active){
    
}
