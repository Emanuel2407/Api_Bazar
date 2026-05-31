package com.bazar.apibazar.dto.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para transferir los datos del usuario en el proceso de login
 */
public record UserLoginRequestDto(@NotBlank String username,
                                  @NotBlank String password) { }
