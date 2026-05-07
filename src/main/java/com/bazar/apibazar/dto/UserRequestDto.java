package com.bazar.apibazar.dto;

import java.util.List;

//Dto para transferir los datos de un usuario cuando se desea registrar
public record UserRequestDto(String username, String password, List<Long> rolesIds) {
}
