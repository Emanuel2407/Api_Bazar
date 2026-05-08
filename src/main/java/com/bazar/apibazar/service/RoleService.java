package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.role.RoleRequestDto;
import com.bazar.apibazar.dto.role.RoleResponseDto;
import com.bazar.apibazar.exception.RoleNotFoundException;
import com.bazar.apibazar.model.Role;
import com.bazar.apibazar.repository.IRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

//Service para desarrollar toda la lógica de negocio que respecta al dominio <<Role>>
@Service
public class RoleService implements IRoleService{

    //Inyección de dependencia para el repositorio de persistencia de <<Role>>
    private final IRoleRepository roleRepo;
    //Inyección de dependencia para PermissionService
    private final PermissionService permissionService;
    //Inyección de dependencia por constructor
    public RoleService(IRoleRepository roleRepo, PermissionService permissionService){
        this.roleRepo = roleRepo;
        this.permissionService = permissionService;
    }

    //Método para construir, a partir de los datos de un <<Role>>, un DTO para exponer ese <<Role>>
    private RoleResponseDto buildRoleResponse(Role objRole){
        //Sacamos instancia de RoleResponseDto
        return new RoleResponseDto(
                objRole.getId(), objRole.getName(),
                    /*Como los permisos dentro de este DTO también son objetos DTO, debemos llamar al service de Permission
                       para construir la lista de DTO a partir de los datos de los permisos que están dentro del rol*/
                    permissionService.buildPermissionsResponse(
                            new ArrayList<>(objRole.getListPermissions())
                    )
        );
    }

    //Método para construir una lista de objetos DTO de respuesta a partir de los datos de una lista de <<Roles>>
    public List<RoleResponseDto> buildRolesResponse(List<Role> listRoles){
        //Lista de objetos DTO a exponer
        List<RoleResponseDto> rolesExponer = new ArrayList<>();

        //Recorremos la lista con el método .forEach()
        listRoles.forEach(
                //Usamos una función lambda que construya cada DTO a partir de los roles de la lista "listRoles" y lo agregue a "rolesExponer"
                role -> rolesExponer.add(buildRoleResponse(role))
        );

        return rolesExponer;
    }

    //Método para consultar los datos de un role por su id y en caso de no encontrarlo lanzar excepción personalizada
    @Transactional(readOnly = true)
    private Role findRole(Long id){
        return roleRepo.findById(id)
                .orElseThrow(
                        //Creamos función lambda para retornar la excepción en caso de que no se encuentre el rol
                        () -> new RoleNotFoundException("No se encontró role con Id: + id")
                );
    }

    @Transactional(readOnly = true)
    @Override
    public List<Role> findAllRolesById(List<Long> rolesIds){
        //Lista de roles encontrados
        List<Role> foundRoles = roleRepo.findAllById(rolesIds);

        //Validamos que se haya hecho la carga total de los roles solicitados
        if(foundRoles.size() < rolesIds.size()){
            throw new RoleNotFoundException("Uno o varios roles no fueron encontrados");
        }

        return foundRoles;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RoleResponseDto> findAllRole() {
        return buildRolesResponse(roleRepo.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public RoleResponseDto findRoleById(Long id) {
        //Buscamos al rol por su ID
        Role objRole = findRole(id);

        //Exponemos al cliente por medio de DTO
        return buildRoleResponse(objRole);
    }

    @Transactional
    @Override
    public RoleResponseDto saveRole(RoleRequestDto newRole) {
        //Construimos el objeto del rol que se va a persistir
        Role objRole = new Role(null, newRole.roleName(),
                new LinkedHashSet<>(
                        /*Como el DTO "RoleRequestDto" tiene dentro una lista de ids de permisos que le vamos a asignar a
                          este rol, debemos buscar esos permisos*/
                        permissionService.findAllPermissionsById(newRole.permissionsIds()))
        );

        //Persistimos objeto
        Role persistedRole = roleRepo.save(objRole);

        return buildRoleResponse(persistedRole);
    }

    @Transactional
    @Override
    public void deleteRol(Long id) {
        //Buscamos rol para verificar existencia
        Role objRole = findRole(id);

        roleRepo.delete(objRole);
    }

    @Transactional
    @Override
    public RoleResponseDto updateRole(Long idRole, RoleRequestDto updatedRol) {
        //Buscamos rol para verificar existencia
        Role objRole = findRole(idRole);

        //Si se quiere actualizar el nombre del rol, se actualiza
        if(updatedRol.roleName() != null){objRole.setName(updatedRol.roleName());}

        //Construimos DTO a partir del rol actualizado
        return buildRoleResponse(objRole);
    }

    @Transactional
    @Override
    public RoleResponseDto addPermissionsToRole(Long idRole, List<Long> newPermissionsIds) {
        //Buscamos rol para confirmar existencia
        Role objRole = findRole(idRole);

        //Buscamos y agregamos nueva lista de permisos al rol
        objRole.getListPermissions().addAll(
                permissionService.findAllPermissionsById(newPermissionsIds)
        );

        //Exponemos rol actualizado
        return buildRoleResponse(objRole);
    }

    @Transactional
    @Override
    public RoleResponseDto removePermissionsFromRole(Long idRole, List<Long> removePermissionsIds) {
        //Buscamos rol para confirmar existencia
        Role objRole = findRole(idRole);

        //Buscamos permissions para confirmar existencia de todos
        permissionService.findAllPermissionsById(removePermissionsIds);

        /*Usamos el método .removeIf() y una función lambda para eliminar todo permiso que cumpla la condición definida:
           Si el id del permiso en cuestión hace parte de la lista de ids de permisos que se mandaron a eliminar*/
        objRole.getListPermissions().removeIf(
                permission -> removePermissionsIds.contains(permission.getId())
        ) ;

        return buildRoleResponse(objRole);
    }
}
