package com.bazar.apibazar.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class VentaDeClienteDto {
    
    private Long idVenta;
    private LocalDate fechaVenta;
    private Double totalVenta;
    private Integer cantidadTotalProductos;
    private List<ProductoDeVentaDto> listProductos; 

    public VentaDeClienteDto() {
    }

    public VentaDeClienteDto(Long idVenta, LocalDate fechaVenta, Double totalVenta, Integer cantidadTotalProductos, List<ProductoDeVentaDto> listProductos) {
        this.idVenta = idVenta;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
        this.cantidadTotalProductos = cantidadTotalProductos;
        this.listProductos = listProductos;
    }
    
    
}
