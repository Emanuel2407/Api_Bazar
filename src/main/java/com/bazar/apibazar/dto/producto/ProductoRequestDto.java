package com.bazar.apibazar.dto.producto;

import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class ProductoRequestDto {
    
    private String nombre;
    private String marca;
    private Double costo;
    private Integer cantidadDisponible;

    public ProductoRequestDto() {
    }

    public ProductoRequestDto(String nombre, String marca, Double costo, int cantidadDisponible) {
        this.nombre = nombre;
        this.marca = marca;
        this.costo = costo;
        this.cantidadDisponible = cantidadDisponible;
    }
    
    
}
