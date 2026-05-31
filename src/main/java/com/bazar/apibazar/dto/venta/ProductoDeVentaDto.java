package com.bazar.apibazar.dto.venta;

/**
 * DTO que expone los datos de los productos
 * incluidos en una venta.
 */
public record ProductoDeVentaDto(
        Long idProducto,
        String nombre,
        String marca,
        Double costo,
        Integer cantidadComprada,
        Double subTotalVenta){}
