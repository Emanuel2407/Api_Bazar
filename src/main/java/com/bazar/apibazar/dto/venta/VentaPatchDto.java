package com.bazar.apibazar.dto.venta;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Dto para actualizar parcialmente los datos de una venta
public record VentaPatchDto(
        LocalDate fechaVenta,
        @Size(min = 1)  //Si se manda a actualizar la lista de productos de una venta, esta lista debe tener mínimo un elemento
        List<@Valid VentaProductoDto> listProductos
) {
    public VentaPatchDto {
        if (listProductos == null) listProductos = new ArrayList<>();
    }
}