package com.bazar.apibazar.dto.producto;

//DTO para exponer un producto al cliente
public record ProductoResponseDto(
        Long idProducto,
        String nombre,
        String marca,
        Double costo,
        Integer cantidadDisponible,
        boolean available) {
}
