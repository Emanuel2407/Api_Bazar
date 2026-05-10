package com.bazar.apibazar.dto.user;

import jakarta.validation.constraints.NotBlank;

//DTO para transferir los datos de un usuario en el proceso de login
//Validamos que se envíe tanto el usuario como la contraseña
public record UserLoginRequestDto(@NotBlank String username,@NotBlank String password) {
}
