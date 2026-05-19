package com.bazar.apibazar.dto.user;

import com.bazar.apibazar.dto.cliente.ClienteRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//DTO para transferir los datos de los usuarios que se registren por el registro público (clientes)
public record ClientUserRequestDto(@NotBlank String username,
                                   @NotBlank String password,
                                   @NotNull @Valid ClienteRequestDto clientData) {
}
