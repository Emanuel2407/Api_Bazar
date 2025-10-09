package com.bazar.apibazar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class ProductoDeVentaDto {
    
    private Long id;
    private String nombre;
    private String marca;
    private Double costo;
    private Integer cantidadComprada;
    private Double subTotalVenta;

    public ProductoDeVentaDto() {
    }

    public ProductoDeVentaDto(Long id, String nombre, String marca, Double costo, Integer cantidadComprada, Double subTotalVenta) {
        this.id = id;
        this.nombre = nombre;
        this.marca = marca;
        this.costo = costo;
        this.cantidadComprada = cantidadComprada;
        this.subTotalVenta = subTotalVenta;
    }

    
    
    
    
}
