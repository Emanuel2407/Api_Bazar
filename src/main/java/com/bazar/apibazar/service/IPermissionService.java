package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.permission.PermissionRequestDto;
import com.bazar.apibazar.dto.permission.PermissionResponseDto;
import com.bazar.apibazar.model.Permission;

import java.util.List;

//Interfaz para definir las operaciones del dominio de 'Permission'
public interface IPermissionService {

    //Mapear objetos Permission a DTOs de respuesta
    List<PermissionResponseDto> buildPermissionsResponse(List<Permission> listPermissions);

    //Traer todos
    List<PermissionResponseDto>  findAllPermissions();

    //Traer una lista de permisos por sus ids
    List<Permission> findAllPermissionsById(List<Long> permissionIds);

    //Encontrar por ID
    PermissionResponseDto findPermissionById(Long id);

    //Registrar nuevo permiso
    PermissionResponseDto savePermission(PermissionRequestDto newPermission);

    //Eliminar permiso por ID
    void deletePermission(Long id);

    //Actualizar permiso
    PermissionResponseDto updatePermission(Long id, PermissionRequestDto updatedPermission);
}
