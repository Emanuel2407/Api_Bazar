package com.bazar.apibazar.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

//DTO para transferir los datos de un role cuando se desea registrar
public record RoleRequestDto(@NotBlank String roleName,
                             @NotEmpty List<@NotBlank String> permissionsNames) {
}
