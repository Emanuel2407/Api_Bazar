package com.bazar.apibazar.dto;

import com.bazar.apibazar.model.Cliente;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class GetVentaDto {
    
    private Long idVenta;
    private LocalDate fechaVenta;
    private Double totalVenta;
    private Integer cantidadTotalProductos;
    private List<ProductoDeVentaDto> listProductos; 
    private Cliente cliente;

    public GetVentaDto() {
    }

    public GetVentaDto(Long idVenta, LocalDate fechaVenta, Double totalVenta, Integer cantidadTotalProductos, List<ProductoDeVentaDto> listProductos, Cliente cliente) {
        this.idVenta = idVenta;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
        this.cantidadTotalProductos = cantidadTotalProductos;
        this.listProductos = listProductos;
        this.cliente = cliente;
    }
    
    
    
}
