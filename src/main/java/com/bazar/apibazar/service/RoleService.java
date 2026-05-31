package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.role.RoleRequestDto;
import com.bazar.apibazar.dto.role.RoleResponseDto;
import com.bazar.apibazar.exception.RoleNotFoundException;
import com.bazar.apibazar.model.Role;
import com.bazar.apibazar.repository.IRoleRepository;
import com.bazar.apibazar.repository.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService implements IRoleService{

    private final IRoleRepository roleRepo;
    private final IPermissionService permissionService;

    public RoleService(IRoleRepository roleRepo, PermissionService permissionService){
        this.roleRepo = roleRepo;
        this.permissionService = permissionService;
    }

    /**
     * Construye DTO de respuesta para exponer un rol.
     */
    private RoleResponseDto buildRoleResponse(Role objRole){
        return new RoleResponseDto(
                objRole.getId(), objRole.getName(),
                    //Construye DTO para exponer permisos.
                    permissionService.buildPermissionsResponse(
                            new ArrayList<>(objRole.getListPermissions())
                    ),
                objRole.isActive()
        );
    }

    private void validarEstadoDeRol(Role objRole){
        if(!objRole.isActive()){throw new RoleNotFoundException("No se encontró rol con nombre: " + objRole.getName() + " disponible");}
    }

    /**
     * Valida que todos los roles de una lista
     * estén activos.
     */
    @Override
    public void validarEstadoDeRoles(List<Role> roles){
        roles.forEach(
                this::validarEstadoDeRol
        );
    }

    /**
     * Construye una lista de DTO para exponerlos
     * */
    @Override
    public List<RoleResponseDto> buildRolesResponse(List<Role> listRoles) {

        List<RoleResponseDto> rolesExponer = new ArrayList<>();

        listRoles.forEach(
                role -> rolesExponer.add(buildRoleResponse(role))
        );

        return rolesExponer;
    }

    /**
     * Consulta rol por su id o lanza excepción si no existe
     * */
    private Role findRole(Long id){
        return roleRepo.findById(id)
                .orElseThrow(
                        () -> new RoleNotFoundException("No se encontró role con Id: " + id)
                );
    }

    @Transactional(readOnly = true)
    @Override
    public List<Role> findAllRolesByNames(Set<String> rolesNames){

        List<Role> foundRoles = roleRepo.findAllByNameIn(rolesNames);

        if(foundRoles.size() < rolesNames.size()){
            throw new RoleNotFoundException("Uno o varios roles no fueron encontrados");
        }

        return foundRoles;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RoleResponseDto> findAllRoles() {
        return buildRolesResponse(roleRepo.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public RoleResponseDto findRoleById(Long id) {

        Role objRole = findRole(id);

        return buildRoleResponse(objRole);
    }

    /**
     * Busca rol por su nombre o lanza excepción si no existe.
     */
    @Override
    public Role findRoleByName(String roleName) {
        return roleRepo.findByName(roleName)
                .orElseThrow(
                        () -> new RoleNotFoundException("No se encontró rol con nombre: " + roleName)
                );
    }

    @Transactional
    @Override
    public RoleResponseDto saveRole(RoleRequestDto newRole) {
        Role objRole = new Role(
                null,
                newRole.roleName(),
                new LinkedHashSet<>(
                       //Busca permisos en PermissionService
                        permissionService.findAllPermissionsByNames(
                                new LinkedHashSet<>(newRole.permissionsNames()))
                ),
                true
        );

        Role persistedRole = roleRepo.save(objRole);

        return buildRoleResponse(persistedRole);
    }

    @Transactional
    @Override
    public void disableRole(Long id) {

        Role objRole = findRole(id);

        objRole.setActive(false);
    }

    @Transactional
    @Override
    public RoleResponseDto updateRole(Long idRole, RoleRequestDto updatedRol) {

        Role objRole = findRole(idRole);

        validarEstadoDeRol(objRole);

        //Actualiza nombre del rol si existe el campo en el DTO de request.
        if(updatedRol.roleName() != null){objRole.setName(updatedRol.roleName());}

        return buildRoleResponse(objRole);
    }

    @Transactional
    @Override
    public RoleResponseDto addPermissionsToRole(Long idRole, List<String> newPermissionsNames) {

        Role objRole = findRole(idRole);

        validarEstadoDeRol(objRole);

        //Agrega nuevos permisos
        objRole.getListPermissions().addAll(
                permissionService.findAllPermissionsByNames(
                        new LinkedHashSet<>(newPermissionsNames)
                )
        );

        return buildRoleResponse(objRole);
    }

    @Transactional
    @Override
    public RoleResponseDto removePermissionsFromRole(Long idRole, List<String> removePermissionsNames) {

        Role objRole = findRole(idRole);

        validarEstadoDeRol(objRole);

        //Validan que todos los permisos existan
        permissionService.findAllPermissionsByNames(new LinkedHashSet<>(removePermissionsNames));

        objRole.getListPermissions().removeIf(
                permission -> removePermissionsNames.contains(permission.getName())
        ) ;

        return buildRoleResponse(objRole);
    }
}
