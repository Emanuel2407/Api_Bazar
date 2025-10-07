package com.bazar.apibazar.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter  @Getter
public class VentaProducto {
    
    //Clave embebida de la tabla intermedia
    @EmbeddedId
    private VentaProductoId id = new VentaProductoId();
    
    /*Como Venta tiene una relación bidireccional con VentaProducto debemos usar la annotation @ManyToOne y del
    lado de venta la annotation @OneToMany, además la annotation @MapsId es necesaria para la relación con la 
    clave primaria compuesta de la tabla intermedia*/
    @ManyToOne
    @MapsId("ventaId")
    @JoinColumn(name="venta_id")
    private Venta venta;
    
     /*La relación entre Producto y VentaProducto se mapea desde VentaProducto porque esta tabla intermedia es
    la que contiene la FK hacia prodcuto.
    En JPA el lado dueño de la relación siempre es el que tiene la FK por eso debemos usar el @ManyToOne junto
    con el @JoinColum.
    Además usamos @MapsId porque la FK de VentaProducto está compuesta por las PKs de Venta y Producto por lo
    que necesitamos que Jpa las sincronice*/
    @ManyToOne
    @MapsId("productoId")
    @JoinColumn(name= "producto_id")
    private Producto producto;
    
    /*Acontinuación se van a declarar los atributos propios de la relacipon que dependerán tanto de las ventas 
    como de los productos en cuestón*/
    
    private Double subTotalVenta;
    
    private Integer cantidad;

    public VentaProducto() {
    }

    public VentaProducto(Venta venta, Producto producto, Double subTotalVenta, Integer cantidad) {
        this.venta = venta;
        this.producto = producto;
        this.subTotalVenta = subTotalVenta;
        this.cantidad = cantidad;
    }
    
    
    
    
}
