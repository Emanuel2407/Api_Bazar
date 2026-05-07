package com.bazar.apibazar.dto;

import java.util.List;

//DTO para transferir los datos de un role cuando se desea registrar
public record RoleRequestDto(String roleName, List<Long> permissionsIds) {
}
