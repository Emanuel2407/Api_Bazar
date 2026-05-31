package com.bazar.apibazar.dto.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado para solicitar el cambio de contraseña.
 *
 * El usuario debe proporcionar su contraseña actual
 * para validar su identidad antes de realizar la operación.
 */
public record UpdatePasswordRequestDto(@NotBlank String currentPassword,
                                       @NotBlank String newPassword) {
}
