package com.bazar.apibazar.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Entidad intermedia que representa la relación
 * entre ventas y productos, permitiendo almacenar
 * atributos adicionales propios de la venta,
 * como la cantidad comprada y el subtotal.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VentaProducto {

    // Clave primaria compuesta por los identificadores de la venta y el producto.
    @EmbeddedId
    private VentaProductoId id = new VentaProductoId();

    // Venta asociada a la relación.
    @ManyToOne
    @MapsId("ventaId")
    @JoinColumn(name = "venta_id")
    private Venta venta;

    // Producto asociado a la relación.
    @ManyToOne
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // Valor total correspondiente a este producto dentro de la venta.
    private Double subTotalVenta;

    // Cantidad de unidades vendidas del producto.
    private Integer cantidad;
}
