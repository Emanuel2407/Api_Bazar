package com.bazar.apibazar.dto.venta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que transporta los detalles de
 * los productos que se quieren agregar
 * a una venta.
 * */
@Setter  @Getter
@AllArgsConstructor
@NoArgsConstructor
public class VentaProductoDto {

    @NotNull
    private Long productoId;
    
    private Double subTotalVenta;

    //Validamos que se ingrese una cantidad mayor a cero para evitar inconsistencias al registrar una venta
    @NotNull @Positive
    private Integer cantidad;
}
