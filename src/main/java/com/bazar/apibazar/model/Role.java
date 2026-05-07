package com.bazar.apibazar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

//Entidad para registrar los diferentes roles de los usarios
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //ID auto-incrementable
    private Long Id;
    private String name;
    //Definimos relación ManyToMany con la entidad 'permissions' con carga ansiosa
    @ManyToMany(fetch = FetchType.EAGER)
    //Propiedades de la tabla intermedia que genera la relación
    @JoinTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    Set<Permission> listPermissions = new LinkedHashSet<>();
}

