package com.bazar.apibazar.dto.user;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

//Dto para transferir los datos de un usuario cuando se desea registrar
public record UserRequestDto(@NotBlank String username,@NotBlank String password, List<Long> rolesIds) {
}
