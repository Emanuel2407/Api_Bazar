package com.bazar.apibazar.dto.producto;

public record ProductoRequestDto(String nombre,
         String marca,
         Double costo,
         Integer cantidadDisponible) {

}
