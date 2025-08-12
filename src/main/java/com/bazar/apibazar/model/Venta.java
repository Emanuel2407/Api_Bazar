package com.bazar.apibazar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter  @Getter
@Entity
public class Venta {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idVenta;
    private LocalDate fechaVenta;
    private Double totalVenta;
    private Integer cantidadProductos;
    
    /*Relación n a n con Producto, se implementará por medio de dos relaciones 1 a n, la primera va de Venta a
    la tabla intermedia VentaProducto(relación bidireccional) y la segunda de Producto a la tabla intermedia 
    VentaProducto(relación unidireccional). Debemos usar el mappedBy para hacer la relación bidireccional con 
    el objeto en VentaProducto que en este caso es "venta" */
    @OneToMany(mappedBy= "venta")
    private List<VentaProducto> listProductos;
    
    
    //Relación 1 a 1 con Cliente
    @OneToOne
    @JoinColumn(name="cliente_id", referencedColumnName="idCliente")
    private Cliente cliente;

    public Venta() {
    }

    public Venta(Long idVenta, LocalDate fechaVenta, List<VentaProducto> listProductos, Cliente cliente) {
        this.idVenta = idVenta;
        this.fechaVenta = fechaVenta;
        this.listProductos = listProductos;
        this.cliente = cliente;
    }

    
    
    
}
