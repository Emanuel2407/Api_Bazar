package com.bazar.apibazar.dto.user;

//Dto para transferir los datos de un usuario y el token JWT que se genera cuando este hace login
public record UserLoginResponseDto(String username, String token, String authorities) {
}
