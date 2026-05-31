package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.permission.PermissionRequestDto;
import com.bazar.apibazar.dto.permission.PermissionResponseDto;
import com.bazar.apibazar.model.Permission;

import java.util.List;
import java.util.Set;

public interface IPermissionService {

    List<PermissionResponseDto> buildPermissionsResponse(List<Permission> listPermissions);

    List<PermissionResponseDto>  findAllPermissions();

    /**
     * Consulta una lista de permisos por sus ids.
     */
    List<Permission> findAllPermissionsByNames(Set<String> permissionNames);

    PermissionResponseDto findPermissionById(Long id);

    PermissionResponseDto savePermission(PermissionRequestDto newPermission);

    void deletePermission(Long id);

    //Actualizar permiso
    PermissionResponseDto updatePermission(Long id, PermissionRequestDto updatedPermission);
}
