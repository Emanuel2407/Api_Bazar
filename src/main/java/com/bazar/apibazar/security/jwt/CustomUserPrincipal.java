package com.bazar.apibazar.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Principal personalizado para guardar la identidad del usuario
 * autenticado usada en operaciones de ownership y
 * control de acceso a recursos.
 * */
@Getter  @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserPrincipal {

    private Long userId;
    private Long clientId; 
    private String username;

}