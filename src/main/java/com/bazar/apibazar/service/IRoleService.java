package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.role.RoleRequestDto;
import com.bazar.apibazar.dto.role.RoleResponseDto;

import java.util.List;

//Interfaz donde se definen las operaciones de lógica de negocio para el dominio <<Role>>
public interface IRoleService {

    //Traer todos
    List<RoleResponseDto> findAllRole();

    //Traer por ID
    RoleResponseDto findRoleById(Long id);

    //Registro de un rol
    RoleResponseDto saveRole(RoleRequestDto newRole);

    //Eliminar rol por id
    void deleteRol(Long id);

    //Actualizar rol
    RoleResponseDto updateRole(Long idRole, RoleRequestDto updatedRol);

    //Método para asignarle uno o varios permisos nuevos al role
    RoleResponseDto addPermissionsToRole(Long idRole, List<Long> newPermissionsIds);

    //Eliminar un rol de un permiso
    RoleResponseDto removePermissionFromRole(Long idRole, Long idPermissionEliminar);

}
