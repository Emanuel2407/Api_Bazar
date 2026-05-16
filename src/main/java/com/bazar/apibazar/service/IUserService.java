package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.user.*;
import com.bazar.apibazar.model.UserSec;

import java.util.List;

//Definimos operaciones de dominio para la entidad User
public interface IUserService {

    //Traemos todos
    List<UserResponseDto> findAll();

    //Traemos por ID
    UserResponseDto findUserById(Long id);

    //Buscar usuario por cliente asociado
    UserSec findByClient(Long clientId);

    /*Registrar usuarios administrativos.
      NOTA: no se registran usuario que son clientes por este método*/
    UserResponseDto saveUser(UserRequestDto newUser);

    //Método de registro público de usuarios, cuyos usuarios registrados será catalogados con rol CLIENTE automáticamente
    UserResponseDto registerClientUser(ClientUserRequestDto clientUserDTO);

    //Eliminar usuario por su id
    void deleteUser(Long id);

    //Agregar roles nuevos a un usuario en particular
    UserResponseDto addRolesToUser(Long userId, List<String> newRolesNames);

    //Eliminar roles asignados a un usuario
    UserResponseDto removeRolesFromUser(Long userId, List<String> removeRolesNames);

//    //---Operaciones sensibles---
//    /*Nota: La identidad de este se saca del token JWT y no por parámetros, ya que así nos aseguramos que la cuenta a
//       la que se le está haciendo el cambio si es realemnte la del usuario autenticado que está haciendo la petición*/
//    //Actualizar username de usuario
//    UserResponseDto updateUsername(UpdateUsernameRequestDto objUpdateUser);
//    //Actualizar contraseña de un usuario
//    void updatePassword(UpdatePasswordRequestDto objUpdatePassword);
}
