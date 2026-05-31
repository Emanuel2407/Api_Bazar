package com.bazar.apibazar.dto.user;

import com.bazar.apibazar.dto.cliente.ClienteRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/***
 * DTO utilizado en el registro público de usuarios.
 */
public record ClientUserRequestDto(@NotBlank String username,
                                   @NotBlank String password,
                                   @NotNull @Valid ClienteRequestDto clientData) {
}
