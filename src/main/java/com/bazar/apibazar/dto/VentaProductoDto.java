package com.bazar.apibazar.dto;

import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.model.Venta;
import lombok.Getter;
import lombok.Setter;

@Setter  @Getter
public class VentaProductoDto {

    private Long productoId;
    
    private Double subTotalVenta;
    
    private Integer cantidad;

    
    public VentaProductoDto() {
    }

    public VentaProductoDto(Long productoId, Double subTotalVenta, Integer cantidad) {
        this.productoId = productoId;
        this.subTotalVenta = subTotalVenta;
        this.cantidad = cantidad;
    }

    
    
    
}
