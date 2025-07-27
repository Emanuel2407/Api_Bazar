package com.bazar.apibazar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Producto {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idProducto;
    private String nombre;
    private String marca;
    private Double costo;
    private Integer cantidadDisponible;

    public Producto() {
    }

    public Producto(Long codigoProducto, String nombre, String marca, Double costo, int cantidadDisponible) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.marca = marca;
        this.costo = costo;
        this.cantidadDisponible = cantidadDisponible;
    }

    
    
    
    
}
