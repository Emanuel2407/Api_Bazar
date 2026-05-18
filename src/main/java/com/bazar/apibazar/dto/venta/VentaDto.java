package com.bazar.apibazar.dto.venta;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record VentaDto(
        LocalDate fechaVenta,
        List<VentaProductoDto> listProductos
) {
    public VentaDto {
        if (listProductos == null) listProductos = new ArrayList<>();
    }
}