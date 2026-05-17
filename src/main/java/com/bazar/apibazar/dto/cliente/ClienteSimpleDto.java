package com.bazar.apibazar.dto.cliente;


//DTO para exponer los datos de un cliente
public record ClienteSimpleDto (Long idCliente, String nombre, String apellido, String documento, boolean active){
    
}
