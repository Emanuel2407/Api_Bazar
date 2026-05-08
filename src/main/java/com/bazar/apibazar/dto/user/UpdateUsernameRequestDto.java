package com.bazar.apibazar.dto.user;

//DTO para modificar el username de un usuario (Este debe conocer su contraseña para efectuar el cambio)
public record UpdateUsernameRequestDto(String password, String newUsername) {
}
