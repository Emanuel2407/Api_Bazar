package com.bazar.apibazar.dto.user;

import jakarta.validation.constraints.NotBlank;

//DTO para modificar el username de un usuario (Este debe conocer su contraseña para efectuar el cambio)
public record UpdateUsernameRequestDto(@NotBlank String password,
                                       @NotBlank String newUsername) {
}
