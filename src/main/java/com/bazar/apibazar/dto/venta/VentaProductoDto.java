package com.bazar.apibazar.dto.venta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Setter  @Getter
public class VentaProductoDto {

    @NotNull private Long productoId;
    
    private Double subTotalVenta;

    //Validamos que se ingrese una cantidad mayor a cero para evitar inconsistencias al registrar una venta
    @NotNull @Positive private Integer cantidad;

    
    public VentaProductoDto() {
    }

    public VentaProductoDto(Long productoId, Double subTotalVenta, Integer cantidad) {
        this.productoId = productoId;
        this.subTotalVenta = subTotalVenta;
        this.cantidad = cantidad;
    }

    
    
    
}
