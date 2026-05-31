package com.bazar.apibazar.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter  @Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Venta {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idVenta;
    private LocalDate fechaVenta;
    private Double totalVenta;
    private Integer cantidadTotalProductos;


    //Cliente asociado a la venta
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    //Detalles de productos incluidos en la venta
    @OneToMany(mappedBy= "venta")
    private List<VentaProducto> listProductos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private VentaStatus status;

}
