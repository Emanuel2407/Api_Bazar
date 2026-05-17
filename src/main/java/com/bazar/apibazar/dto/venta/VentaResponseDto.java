package com.bazar.apibazar.dto.venta;

import java.time.LocalDate;
import java.util.List;

import com.bazar.apibazar.dto.cliente.ClienteSimpleDto;
import com.bazar.apibazar.model.VentaStatus;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class VentaResponseDto {
    
    private Long idVenta;
    private LocalDate fechaVenta;
    private Double totalVenta;
    private Integer cantidadTotalProductos;
    private List<ProductoDeVentaDto> listProductos;
    private ClienteSimpleDto cliente;
    private VentaStatus status;

    public VentaResponseDto() {
    }

    public VentaResponseDto(Long idVenta, LocalDate fechaVenta, Double totalVenta, Integer cantidadTotalProductos, List<ProductoDeVentaDto> listProductos, ClienteSimpleDto cliente, VentaStatus status) {
        this.idVenta = idVenta;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
        this.cantidadTotalProductos = cantidadTotalProductos;
        this.listProductos = listProductos;
        this.cliente = cliente;
        this.status = status;
    }





    
}
