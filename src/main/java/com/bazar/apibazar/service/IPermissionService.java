package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.permission.PermissionRequestDto;
import com.bazar.apibazar.dto.permission.PermissionResponseDto;

import java.util.List;

//Interfaz para definir las operaciones del dominio de 'Permission'
public interface IPermissionService {

    //Traer todos
    List<PermissionResponseDto>  findAllPermissions();

    //Encontrar por ID
    PermissionResponseDto findPermissionById(Long id);

    //Registrar nuevo permiso
    PermissionResponseDto savePermission(PermissionRequestDto newPermission);

    //Eliminar permiso por ID
    void deletePermission(Long id);

    //Actualizar permiso
    PermissionResponseDto updatePermission(Long id, PermissionRequestDto updatedPermission);
}
