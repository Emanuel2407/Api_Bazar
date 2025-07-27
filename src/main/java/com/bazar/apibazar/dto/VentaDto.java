package com.bazar.apibazar.dto;

import com.bazar.apibazar.model.Cliente;
import com.bazar.apibazar.model.Producto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class VentaDto {
    
    private LocalDate fechaVenta;
    private List<Producto> listProductos = new ArrayList<>();
    private Cliente cliente;

    public VentaDto() {
    }

    public VentaDto(LocalDate fechaVenta, List<Producto> listProductos, Cliente cliente) {
        this.fechaVenta = fechaVenta;
        this.listProductos = listProductos;
        this.cliente = cliente;
    }

    
    
    
}
