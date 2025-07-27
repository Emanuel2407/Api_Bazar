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
    private Double total;
    
    //Relación 1 a n con Producto
    @OneToMany
    @JoinColumn(name="venta_id", referencedColumnName="idVenta")
    private List<Producto> listProductos;
    
    
    //Relación 1 a 1 con Cliente
    @OneToOne
    @JoinColumn(name="cliente_id", referencedColumnName="idCliente")
    private Cliente cliente;

    public Venta() {
    }

    public Venta(Long idVenta, LocalDate fechaVenta, Double total, List<Producto> listProductos, Cliente cliente) {
        this.idVenta = idVenta;
        this.fechaVenta = fechaVenta;
        this.total = total;
        this.listProductos = listProductos;
        this.cliente = cliente;
    }

    
    
    
}
