package com.bazar.apibazar.dto.role;

import com.bazar.apibazar.dto.permission.PermissionResponseDto;

import java.util.List;

//DTO para transferir los datos de un role al cliente
public record RoleResponseDto(String id, String roleName, List<PermissionResponseDto> permissions) {
}
