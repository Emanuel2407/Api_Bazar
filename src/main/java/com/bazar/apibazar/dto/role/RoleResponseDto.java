package com.bazar.apibazar.dto.role;

import com.bazar.apibazar.dto.permission.PermissionResponseDto;

import java.util.List;

public record RoleResponseDto(Long id, String roleName, List<PermissionResponseDto> permissions, boolean active) {
}
