package com.bazar.apibazar.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa un permiso que puede asignarse a uno o varios roles
 * para autorizar operaciones dentro del sistema.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "permissions")
@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true)
    private String name;

}
