package com.bazar.apibazar.dto.permission;

import jakarta.validation.constraints.NotBlank;

public record PermissionRequestDto(@NotBlank String permissionName) {
}
