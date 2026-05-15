package com.bazar.apibazar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

//Entidad para el registro de usuarios
@Getter  @Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Entity
public class UserSec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //No debe haber dos usuarios con el mismo username
    @Column(unique = true)
    private String username;
    private String password;
    //Relación uno a uno entre clientes y usuarios para identificar a qué cliente de negocio corresponde un usuario cuando este tiene el rol: "ROLE_CLIENTE"
    //Establecemos CascadeType.PERSIST para que cuando Hibernate persista un usuario, si hay un objeto cliente dentro de este y no está registrado, se persista también
    //CascadeType.MERGE se usa en este caso para que cuando se actualice el objeto Cliente dentro de un usuario, también se actualice ese registro en la entidad de clientes
    @OneToOne(cascade = {PERSIST, MERGE})
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    //Relación ManyToMany con tipo de carga ansiosa (Cuando se carga un usuario, se cargan sus roles)
    @ManyToMany(fetch = FetchType.EAGER)
    //Definimos propiedades de tabla intermedia
    //joinColumns() -> FK que hace referencia al lado dueño de la relación (UserSec)
    //inverseJoinColums() -> FK que hace referencia al otro lado, o lado inverso de la relación (Role)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> listRoles = new LinkedHashSet<>();

    //Columnas para gestionar el estado del usuario (Al momento de la creación todas serán positivas)
    private boolean enabled=true;  //Si no ha sido eliminado o deshabilitado
    private boolean accountNotExpired=true; //Si la cuenta no ha expirado
    private boolean accountNotLocked=true;  //Si la cuenta no está bloqueada
    private boolean credentialNotExpired=true;  //Si las credenciales no han expirado

}
