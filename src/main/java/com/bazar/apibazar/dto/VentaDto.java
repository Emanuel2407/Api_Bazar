package com.bazar.apibazar.dto;

import com.bazar.apibazar.model.Cliente;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.model.VentaProducto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class VentaDto {
    
    private LocalDate fechaVenta;
    private List<VentaProductoDto> listProductos = new ArrayList<>();
    private Cliente cliente;
    private Double totalVenta;
    
    public VentaDto() {
    }

    public VentaDto(LocalDate fechaVenta, List<VentaProductoDto>listProductos, Cliente cliente) {
        this.fechaVenta = fechaVenta;
        this.listProductos = listProductos;
        this.cliente = cliente;
    }

    
    
    
}
