package com.bazar.apibazar.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class VentaProducto {
    
    //Clave embebida de la tabla intermedia
    @EmbeddedId
    private VentaProductoId id = new VentaProductoId();
    
    /*Como es una relación bidireccional, usamos la annotation @ManyToOne para la relación con Venta y con 
    Producto, además la annotation @MapsId es necesaria para la relación con la clave primaria compuesta de la 
    tabla intermedia*/
    
    @ManyToOne
    @MapsId("ventaId")
    @JoinColumn(name="venta_id")
    private Venta venta;
    
    @ManyToOne
    @MapsId("productoId")
    @JoinColumn(name= "producto_id")
    private Producto producto;
    
    /*Acontinuación se van a declarar los atributos propios de la relacipon que dependerán tanto de las ventas 
    como de los productos en cuestón*/
    
    private Double totalVenta;
    
    private Integer cantidad;
    
    
}
