package com.bazar.apibazar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Entity
public class UserSec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de usuario único utilizado para autenticación.
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    //Referencia al cliente asociado con el usuario si este tiene rol CLIENT
    @OneToOne(cascade = {PERSIST, MERGE})
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    //Roles asignados al usuario
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> listRoles = new LinkedHashSet<>();

    private boolean enabled=true;
    private boolean accountNotExpired=true;
    private boolean accountNotLocked=true;
    private boolean credentialNotExpired=true;

}