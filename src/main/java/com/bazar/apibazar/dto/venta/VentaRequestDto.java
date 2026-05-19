package com.bazar.apibazar.dto.venta;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record VentaRequestDto(
        LocalDate fechaVenta,
        List<VentaProductoDto> listProductos
) {
    public VentaRequestDto {
        if (listProductos == null) listProductos = new ArrayList<>();
    }
}