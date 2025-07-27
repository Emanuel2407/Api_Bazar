package com.bazar.apibazar.dto;

import lombok.Getter;
import lombok.Setter;

@Setter  @Getter
public class VentaResumenDto {
    
    private Long idVenta;
    private Double total;
    private int cantProductos = 0;
    private String nombreCliente;
    private String apellidoCliente;

    public VentaResumenDto() {
    }

    public VentaResumenDto(Long idVenta, Double total, int cantProductos, String nombreCliente, String apellidoCliente) {
        this.idVenta = idVenta;
        this.total = total;
        this.cantProductos = cantProductos;
        this.nombreCliente = nombreCliente;
        this.apellidoCliente = apellidoCliente;
    }
    
    
    
}
