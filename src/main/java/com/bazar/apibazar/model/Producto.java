package com.bazar.apibazar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
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
    
    /*Relación n a n con Producto, se implementará por medio de dos relaciones 1 a n, la primera va de Venta a
    la tabla intermedia VentaProducto y la segunda de Producto a la tabla intermedia VentaProducto. 
    Debemos usar el mappedBy para hacer la relación con el objeto en VentaProducto que en este caso es "producto" */
    @OneToMany(mappedBy= "producto")
    private List<VentaProducto> listVentas;

    public Producto() {
    }

    public Producto(Long idProducto, String nombre, String marca, Double costo, Integer cantidadDisponible, List<VentaProducto> listVentas) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.marca = marca;
        this.costo = costo;
        this.cantidadDisponible = cantidadDisponible;
        this.listVentas = listVentas;
    }

    

    
    
    
    
}
