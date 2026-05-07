package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.permission.PermissionRequestDto;
import com.bazar.apibazar.dto.permission.PermissionResponseDto;
import com.bazar.apibazar.exception.PermissionNotFoundException;
import com.bazar.apibazar.model.Permission;
import com.bazar.apibazar.repository.IPermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

//Service donde se lleva a cambo la implementación de las operaciones de dominio y se define la lógica de negocio para los permisos
@Service
public class PermissionService implements IPermissionService{

    //Inyección de dependencia para el repositorio de Permission
    private final IPermissionRepository permissionRepo;
    //Inyección de dependencia por constructor
    public PermissionService(IPermissionRepository permissionRepo) {
        this.permissionRepo = permissionRepo;
    }

    //Método propio para exponer los datos de un permiso por medio del DTO: "PermissionResponse"
    private PermissionResponseDto buildPermissionResponse(Permission objPermission){
        return new PermissionResponseDto(objPermission.getId(), objPermission.getName());
    }

    //Método privado para buscar un permiso y en caso de que no existe -> Excepción que lo indica
    private Permission findPermission(Long id){
        return permissionRepo.findById(id)
                .orElseThrow(
                        () -> new PermissionNotFoundException("No se encontró permiso con id: " + id)
                );
    }

    @Transactional(readOnly = true)
    @Override
    public List<PermissionResponseDto> findAllPermissions() {
        //Lista de permisos a exponer
        List<PermissionResponseDto> permissionsResponse = new ArrayList<>();

        //Vamos creando los DTO de los permisos a exponer
        for(Permission objPermission: permissionRepo.findAll()){
            permissionsResponse.add(buildPermissionResponse(objPermission));
        }

        return permissionsResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public PermissionResponseDto findPermissionById(Long id) {
        //Buscamos permiso por su ID
        Permission objPermission = findPermission(id);

        //Se transfieren los datos de este al DTO de exposición
        return buildPermissionResponse(objPermission);
    }

    @Transactional
    @Override
    public PermissionResponseDto savePermission(PermissionRequestDto newPermission) {
        //Objeto que se va a persistir
        Permission objPermission = new Permission(null, newPermission.permissionName());

        //Registramos permiso
        permissionRepo.save(objPermission);

        //Exponemos permiso
        return buildPermissionResponse(objPermission);
    }

    @Transactional
    @Override
    public void deletePermission(Long id) {
        //Buscamos permiso para verificar existencia
        Permission objPermission = findPermission(id);

        permissionRepo.delete(objPermission);
    }

    @Transactional
    @Override
    public PermissionResponseDto updatePermission(Long id, PermissionRequestDto updatedPermission) {
        //Buscamos permiso para verificar existencia
        Permission objPermission = findPermission(id);

        //Actualizamos los campos solicitados
        if(updatedPermission.permissionName() != null){objPermission.setName(updatedPermission.permissionName());}

        //Exponemos permiso actualizado
        return buildPermissionResponse(objPermission);
    }
}
