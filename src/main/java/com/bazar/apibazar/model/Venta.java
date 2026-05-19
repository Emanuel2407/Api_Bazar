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

    //Referencia al cliente dueño de la venta
    /*Definimos relación unidireccional one-to-many (many-to-one del lado de venta) con la entidad cliente para que solo la venta
     conozca el cliente que la realizó, estableciendo a venta como lado dueño*/
    //FetchType fuerza una carga ansiosa, por lo que cuando se busque a una venta, esta vendrá con el cliente que la realizó
    @ManyToOne(fetch = FetchType.EAGER)
    //Definimos nombre de la FK en la table cliente
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    /*Relación n a n con Producto, se implementará por medio de dos relaciones 1 a n, la primera va de Venta a
    la tabla intermedia VentaProducto(relación bidireccional) y la segunda de Producto a la tabla intermedia 
    VentaProducto(relación unidireccional). Debemos usar el mappedBy para hacer la relación bidireccional con 
    el objeto en VentaProducto que en este caso es "venta" */
    @OneToMany(mappedBy= "venta")
    private List<VentaProducto> listProductos = new ArrayList<>();

    //Definimos el estado en que se encuentra la venta
    /*La annotation @Enumerated define la forma en que se guarda este enum en la base de datos.
     * El parámetro EnumType.STRING define que el valor del enum será guardado como String.
     * NOTA: Por defecto Hibernate usa EnumType.ORDINAL, el cual guarda el enum como número. Por ejemplo, el primer valor
     * definido en el enum = 0, el segundo = 1 y así´sucesivamente. Esto es frágil y se considera una mala práctica por lo que
     * siempre es importante definir EnumType.STRING*/
    @Enumerated(EnumType.STRING)
    private VentaStatus status;

}
