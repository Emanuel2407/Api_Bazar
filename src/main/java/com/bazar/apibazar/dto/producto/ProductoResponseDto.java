package com.bazar.apibazar.dto.producto;

public record ProductoResponseDto(
        Long idProducto,
        String nombre,
        String marca,
        Double costo,
        Integer cantidadDisponible,
        boolean available) {
}
