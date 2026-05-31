package com.bazar.apibazar.dto.user;

import java.util.List;

/**
 * DTO que transporta datos del usuario y el token JWT
 * cuando este inicia sesión.
 */
public record UserLoginResponseDto(String username,
                                   String token,
                                   List<String> authorities) {}
