package com.bazar.apibazar.model;

import jakarta.persistence.*;
import lombok.*;

//Entidad encargada del registro de las acciones que los usuarios tienen permitido hacer dentro de la app
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Usamos solo el ID para equals/hashCode. Esto garantiza que JPA y las Colecciones (como el Set en Role)
// identifiquen correctamente la entidad por su valor en BD y no por su instancia en memoria.
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
//Parámetros de la tabla: name=permissions
@Table(name = "permissions")
@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    //No tiene sentido tener un permiso repetido
    @Column(unique = true)
    private String name;

}
