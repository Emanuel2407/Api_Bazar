package com.bazar.apibazar.dto;

import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.model.Venta;
import lombok.Getter;
import lombok.Setter;

@Setter  @Getter
public class VentaProductoDto {

    private Long productoId;
    
    private Double totalVenta;
    
    private Integer cantidad;

    
    public VentaProductoDto() {
    }

    public VentaProductoDto(Long productoId, Double totalVenta, Integer cantidad) {
        this.productoId = productoId;
        this.totalVenta = totalVenta;
        this.cantidad = cantidad;
    }

    
    
    
}
