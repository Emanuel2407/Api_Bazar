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

//Service para desarrollar toda la lógica de negocio que respecta al dominio <<Role>>
@Service
public class RoleService implements IRoleService{

    //Inyección de dependencia para el repositorio de persistencia de <<Role>>
    private final IRoleRepository roleRepo;
    //Inyección de dependencia para PermissionService
    private final IPermissionService permissionService;
    //Inyección de dependencia para el repositorio de persistencia de usuarios
    private final IUserRepository userRepo;
    //Inyección de dependencia por constructor
    public RoleService(IRoleRepository roleRepo, PermissionService permissionService, IUserRepository userRepo){
        this.roleRepo = roleRepo;
        this.permissionService = permissionService;
        this.userRepo = userRepo;
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
    @Override
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
    private Role findRole(Long id){
        return roleRepo.findById(id)
                .orElseThrow(
                        //Creamos función lambda para retornar la excepción en caso de que no se encuentre el rol
                        () -> new RoleNotFoundException("No se encontró role con Id: " + id)
                );
    }

    @Transactional(readOnly = true)
    @Override
    public List<Role> findAllRolesByNames(Set<String> rolesNames){
        //Lista de roles encontrados
        List<Role> foundRoles = roleRepo.findAllByNameIn(rolesNames);

        //Validamos que se haya hecho la carga total de los roles solicitados
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
                        permissionService.findAllPermissionsByNames(new LinkedHashSet<>(newRole.permissionsNames())))
        );

        //Persistimos objeto
        Role persistedRole = roleRepo.save(objRole);

        return buildRoleResponse(persistedRole);
    }

    @Transactional
    @Override
    public void deleteRole(Long id) {
        //Buscamos rol para verificar existencia
        Role objRole = findRole(id);

        //Debemos eliminar las relaciones que este rol tenga con los usuarios (registros en la tabla intermedia)
        /*Para ello simplemente removemos este rol de la lista de roles de los usuarios con los que está relacionado,
          el contexto de persistencia detectará el cambio y al final de la transacción Hibernate eliminará las relaciones*/
        userRepo.findByListRoles_id(id).forEach(
                //Creamos función lambda para que se remueva el rol de cada usuario que lo contiene en su "listRoles"
                user -> user.getListRoles().remove(objRole)
        );

        //Finalmente, podremos eliminar el rol
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
    public RoleResponseDto addPermissionsToRole(Long idRole, List<String> newPermissionsNames) {
        //Buscamos rol para confirmar existencia
        Role objRole = findRole(idRole);

        //Buscamos y agregamos nueva lista de permisos al rol
        objRole.getListPermissions().addAll(
                permissionService.findAllPermissionsByNames(new LinkedHashSet<>(newPermissionsNames))
        );

        //Exponemos rol actualizado
        return buildRoleResponse(objRole);
    }

    @Transactional
    @Override
    public RoleResponseDto removePermissionsFromRole(Long idRole, List<String> removePermissionsNames) {
        //Buscamos rol para confirmar existencia
        Role objRole = findRole(idRole);

        //Buscamos permissions para confirmar existencia de todos
        permissionService.findAllPermissionsByNames(new LinkedHashSet<>(removePermissionsNames));

        /*Usamos el método .removeIf() y una función lambda para eliminar todo permiso que cumpla la condición definida:
           Si el id del permiso en cuestión hace parte de la lista de ids de permisos que se mandaron a eliminar*/
        objRole.getListPermissions().removeIf(
                permission -> removePermissionsNames.contains(permission.getName())
        ) ;

        return buildRoleResponse(objRole);
    }
}
