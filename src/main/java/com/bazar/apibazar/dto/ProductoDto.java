package com.bazar.apibazar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class ProductoDto {
    
    private String nombre;
    private String marca;
    private Double costo;
    private Integer cantidadDisponible;

    public ProductoDto() {
    }

    public ProductoDto(String nombre, String marca, Double costo, int cantidadDisponible) {
        this.nombre = nombre;
        this.marca = marca;
        this.costo = costo;
        this.cantidadDisponible = cantidadDisponible;
    }
    
    
}
