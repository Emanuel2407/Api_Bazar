package com.bazar.apibazar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter @Setter
public class Producto {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idProducto;
    private String nombre;
    private String marca;
    private Double costo;
    private Integer cantidadDisponible;
    //Cuando un producto se mande a retirar del mercado, no se eliminarán sus datos sino que se marcará como "available=false" (SoftDelete)
    private boolean available=true;

    public Producto() {
    }

    public Producto(Long idProducto, String nombre, String marca, Double costo, Integer cantidadDisponible) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.marca = marca;
        this.costo = costo;
        this.cantidadDisponible = cantidadDisponible;
    }

    

    
    
    
    
}
