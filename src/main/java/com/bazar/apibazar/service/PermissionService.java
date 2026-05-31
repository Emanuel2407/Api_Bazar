package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.permission.PermissionRequestDto;
import com.bazar.apibazar.dto.permission.PermissionResponseDto;
import com.bazar.apibazar.exception.PermissionNotFoundException;
import com.bazar.apibazar.model.Permission;
import com.bazar.apibazar.model.Role;
import com.bazar.apibazar.repository.IPermissionRepository;
import com.bazar.apibazar.repository.IRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PermissionService implements IPermissionService{

    private final IPermissionRepository permissionRepo;
    private final IRoleRepository rolRepo;

    public PermissionService(IPermissionRepository permissionRepo, IRoleRepository rolRepo) {
        this.permissionRepo = permissionRepo;
        this.rolRepo = rolRepo;
    }

    /**
     * Construye DTO para exponer un permiso
     */
    private PermissionResponseDto buildPermissionResponse(Permission objPermission){
        return new PermissionResponseDto(objPermission.getId(), objPermission.getName());
    }

    /**
     * Consulta un permiso o lanza excepción de dominio si no existe.
     */
    private Permission findPermission(Long id){
        return permissionRepo.findById(id)
                .orElseThrow(
                        () -> new PermissionNotFoundException("No se encontró permiso con id: " + id)
                );
    }

   /**
    * Construye DTOs de repuesta para exponer
    * una lista de permisos.
    */
    @Override
    public List<PermissionResponseDto> buildPermissionsResponse(List<Permission> listPermissions){
        List<PermissionResponseDto> permisosExponer = new ArrayList<>();

        listPermissions.forEach(
                permission -> permisosExponer.add(
                        buildPermissionResponse(permission)
                )
        );

        return permisosExponer;
    }

    /**
     * Consulta los datos de una lista de
     * permisos por sus ids.
     * */
    @Transactional(readOnly = true)
    @Override
    public List<Permission> findAllPermissionsByNames(Set<String> rolesNames){
        List<Permission> foundPermissions = permissionRepo.findAllByNameIn(rolesNames);

        //Validamos la carga de todos los permisos
        if(foundPermissions.size() < rolesNames.size()){throw new PermissionNotFoundException("Uno o varios permisos no fueron encontrados");}

        return foundPermissions;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PermissionResponseDto> findAllPermissions() {

        return buildPermissionsResponse(permissionRepo.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public PermissionResponseDto findPermissionById(Long id) {

        Permission objPermission = findPermission(id);

        return buildPermissionResponse(objPermission);
    }

    @Transactional
    @Override
    public PermissionResponseDto savePermission(PermissionRequestDto newPermission) {

        Permission objPermission = new Permission(null, newPermission.permissionName());

        permissionRepo.save(objPermission);

        return buildPermissionResponse(objPermission);
    }

    @Transactional
    @Override
    public void deletePermission(Long id) {

        Permission objPermission = findPermission(id);

        //Elimina relaciones del permiso con los diferentes roles
        rolRepo.findByListPermissions_id(id).forEach(
                role -> role.getListPermissions().remove(objPermission)
        );

        permissionRepo.delete(objPermission);
    }

    @Transactional
    @Override
    public PermissionResponseDto updatePermission(Long id, PermissionRequestDto updatedPermission) {
        Permission objPermission = findPermission(id);

        if(updatedPermission.permissionName() != null){objPermission.setName(updatedPermission.permissionName());}

        return buildPermissionResponse(objPermission);
    }
}
