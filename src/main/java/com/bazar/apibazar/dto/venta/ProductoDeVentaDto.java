package com.bazar.apibazar.dto.venta;

public record ProductoDeVentaDto(
        Long idProducto,
        String nombre,
        String marca,
        Double costo,
        Integer cantidadComprada,
        Double subTotalVenta){}
