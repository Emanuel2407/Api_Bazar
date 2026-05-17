package com.bazar.apibazar.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter  @Setter
@AllArgsConstructor
@NoArgsConstructor
//Clase para sacar objetos Principal personalizados que guarden la identidad autenticada del usuario para ownership y acceso contextual
public class CustomUserPrincipal {

    private Long userId;
    private Long clientId;  //En caso de que el usuario no sea cliente, este campo será null
    private String username;

}