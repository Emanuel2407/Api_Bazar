package com.bazar.apibazar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
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
    //Cuando un producto se mande a retirar del mercado, no se eliminarán sus datos sino que se marcará como "available=false" (SoftDelete)
    private boolean available=true;

}
