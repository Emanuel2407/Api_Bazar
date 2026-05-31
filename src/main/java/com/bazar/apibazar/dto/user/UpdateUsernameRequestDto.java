package com.bazar.apibazar.dto.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado para solicitar el cambio de nombre de usuario.
 *
 * El usuario debe proporcionar su contraseña actual
 * para validar su identidad antes de realizar la operación.
 */
public record UpdateUsernameRequestDto(@NotBlank String password,
                                       @NotBlank String newUsername) {
}
