package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.role.RoleRequestDto;
import com.bazar.apibazar.dto.role.RoleResponseDto;
import com.bazar.apibazar.model.Role;

import java.util.List;

//Interfaz donde se definen las operaciones de lógica de negocio para el dominio <<Role>> que serán usadas por diferentes clientes (controllers, otros service, etc)
public interface IRoleService {

    //Traer todos
    List<RoleResponseDto> findAllRoles();

    //Traer lista de roles por sus ids
    List<Role> findAllRolesById(List<Long> rolesIds);

    //Mapear objetos Roles a DTOs de respuesta para exposición
    List<RoleResponseDto> buildRolesResponse(List<Role> listRoles);

    //Traer por ID
    RoleResponseDto findRoleById(Long id);

    //Registro de un rol
    RoleResponseDto saveRole(RoleRequestDto newRole);

    //Eliminar rol por id
    void deleteRole(Long id);

    //Actualizar rol
    RoleResponseDto updateRole(Long idRole, RoleRequestDto updatedRol);

    //Método para asignarle uno o varios permisos nuevos al role
    RoleResponseDto addPermissionsToRole(Long idRole, List<Long> newPermissionsIds);

    //Eliminar una lista de permisos dentro de un rol
    RoleResponseDto removePermissionsFromRole(Long idRole, List<Long> removePermissionsIds);

}
