package com.bazar.apibazar.model;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

import lombok.*;

/**
 * Objeto embebido que representa la
 * primary key compuesta de la tabla intermedia VentaProducto
 */
@Embeddable
@EqualsAndHashCode
@Getter  @Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaProductoId implements Serializable{

    //Referencias a las dos tablas relacionadas.
    private Long ventaId;
    private Long productoId;

}
