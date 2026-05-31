package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.user.*;
import com.bazar.apibazar.model.UserSec;

import java.util.List;

public interface IUserService {

    List<UserResponseDto> findAll();

    UserResponseDto findUserById(Long id);

    /**
     * Busca usuario por cliente asociado
     */
    UserSec findByClient(Long clientId);

    /**
     * Registro administrativo de usuarios.
     *
     * No se registran usuario que son clientes por este flujo
     */
    UserResponseDto saveUser(UserRequestDto newUser);

    /**
     * Registro público de usuarios, cuyos
     * usuarios registrados serán catalogados con rol "CLIENT" automáticamente.
     */
    UserResponseDto registerClientUser(ClientUserRequestDto clientUserDTO);

    void disableUser(Long id);

    UserResponseDto addRolesToUser(Long userId, List<String> newRolesNames);

    UserResponseDto removeRolesFromUser(Long userId, List<String> removeRolesNames);

    /**
     * Consulta los datos del usuario que está autenticado en el contexto de seguridad
     */
    UserResponseDto findMe();

    /**
     * Actualizar username de usuario autenticado
     */
    UserResponseDto updateUsername(UpdateUsernameRequestDto objUpdateUsername);

    /**
     *  Actualizar contraseña de usuario autenticado
     */
    void updatePassword(UpdatePasswordRequestDto objUpdatePassword);
}
