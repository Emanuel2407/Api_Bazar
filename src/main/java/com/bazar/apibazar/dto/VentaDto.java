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
    
    public VentaDto() {
    }

    public VentaDto(LocalDate fechaVenta, List<VentaProductoDto> listProductos) {
        this.fechaVenta = fechaVenta;
        this.listProductos = listProductos;
    }

   

    

    
    
    
}
