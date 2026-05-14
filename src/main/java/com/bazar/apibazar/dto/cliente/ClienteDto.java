package com.bazar.apibazar.dto.cliente;


//DTO para recibir los datos de un cliente cuando se va a hacer un registro o una modificación de este
public record ClienteDto(String nombre,
                         String apellido,
                         String documento){}
