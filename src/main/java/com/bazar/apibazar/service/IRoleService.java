package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.role.RoleRequestDto;
import com.bazar.apibazar.dto.role.RoleResponseDto;
import com.bazar.apibazar.model.Role;

import java.util.List;
import java.util.Set;

public interface IRoleService {

    List<RoleResponseDto> findAllRoles();

    void validarEstadoDeRoles(List<Role> roles);

    List<Role> findAllRolesByNames(Set<String> rolesNames);

    List<RoleResponseDto> buildRolesResponse(List<Role> listRoles);

    RoleResponseDto findRoleById(Long id);

    Role findRoleByName(String roleName);

    RoleResponseDto saveRole(RoleRequestDto newRole);

    void disableRole(Long id);

    RoleResponseDto updateRole(Long idRole, RoleRequestDto updatedRol);

    RoleResponseDto addPermissionsToRole(Long idRole, List<String> newPermissionsNames);

    RoleResponseDto removePermissionsFromRole(Long idRole, List<String> removePermissionsNames);

}
