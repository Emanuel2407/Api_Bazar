package com.bazar.apibazar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Entidad encargada del registro de las acciones que los usuarios tienen permitido hacer dentro de la app
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//Parámetros de la tabla: name=permissions
@Table(name = "permissions")
@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //No tiene sentido tener un permiso repetido
    @Column(unique = true)
    private String name;

}
