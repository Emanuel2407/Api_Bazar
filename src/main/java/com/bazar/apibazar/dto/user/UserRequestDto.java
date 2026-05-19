package com.bazar.apibazar.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

//Dto para transferir los datos de un usuario cuando se desea registrar
public record UserRequestDto(@NotBlank String username,
                             @NotBlank String password,
                             @NotEmpty List<@NotBlank String> rolesNames) {}
