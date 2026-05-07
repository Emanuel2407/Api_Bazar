package com.bazar.apibazar.dto;

import java.util.List;

//DTO para exponer los datos de un usuario al cliente
public record UserResponseDto(Long id, String username, List<RoleResponseDto> roles) {
}
