package com.bazar.apibazar.dto.user;

import jakarta.validation.constraints.NotBlank;

/*DTO enfocado en el cambio de contraseña de un usuario, este debe cumplir ciertos requisitos para efectuar este cambio
 (Como que debe conocer su contraseña actual para poder modificarla)*/
public record UpdatePasswordRequestDto(@NotBlank String currentPassword,
                                       @NotBlank String newPassword) {
}
