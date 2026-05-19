package com.bazar.apibazar.dto.permission;

import jakarta.validation.constraints.NotBlank;

//DTO para transferir los datos de un permiso que se desea registrar
public record PermissionRequestDto(@NotBlank String permissionName) {
}
