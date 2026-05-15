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

//Service donde se lleva a cambo la implementación de las operaciones de dominio y se define la lógica de negocio para los permisos
@Service
public class PermissionService implements IPermissionService{

    //Inyección de dependencia para el repositorio de Permission
    private final IPermissionRepository permissionRepo;
    //Inyección de dependencia para acceder a consultas simples de persistencia del componente <<Role>>
    private final IRoleRepository rolRepo;
    //Inyección de dependencia por constructor
    public PermissionService(IPermissionRepository permissionRepo, IRoleRepository rolRepo) {
        this.permissionRepo = permissionRepo;
        this.rolRepo = rolRepo;
    }

    //Método propio para exponer los datos de un permiso por medio del DTO: "PermissionResponse"
    private PermissionResponseDto buildPermissionResponse(Permission objPermission){
        return new PermissionResponseDto(objPermission.getId(), objPermission.getName());
    }

    //Método privado para buscar un permiso y en caso de que no existe -> Excepción que lo indica
    private Permission findPermission(Long id){
        return permissionRepo.findById(id)
                .orElseThrow(
                        //Programación funcional para simplificar el proceso
                        () -> new PermissionNotFoundException("No se encontró permiso con id: " + id)
                );
    }

    //Método propio para construir objetos DTO de respuesta para exponerlos creados a partir de una lista de permisos
    @Override
    public List<PermissionResponseDto> buildPermissionsResponse(List<Permission> listPermissions){
        List<PermissionResponseDto> permisosExponer = new ArrayList<>();

        //Recorremos la lista y usamos programación funcional para construir cada objeto DTO a partir de los datos de cada permiso
        listPermissions.forEach(
                permission -> permisosExponer.add(buildPermissionResponse(permission))
        );

        return permisosExponer;
    }

    //Método para consultar una lista de permisos por sus ids
    @Transactional(readOnly = true)
    @Override
    public List<Permission> findAllPermissionsByNames(Set<String> rolesNames){
        List<Permission> foundPermissions = permissionRepo.findAllByNameIn(rolesNames);

        //En caso de que no se encuentren todos los permisos que se consultaron, excepción indicándolo
        if(foundPermissions.size() < rolesNames.size()){throw new PermissionNotFoundException("Uno o varios permisos no fueron encontrados");}

        return foundPermissions;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PermissionResponseDto> findAllPermissions() {
        //Usamos el método que construya la lista de DTO a partir de los permisos registrados
        return buildPermissionsResponse(permissionRepo.findAll());
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

        //Buscamos roles relacionados con este permiso para eliminar cada una de las asociaciones
        rolRepo.findByListPermissions_id(id).forEach(
                //Usamos función lambda para sacar de cada rol el permiso que vamos a eliminar y asi se eliminará el registro de la relación en la tabla intermedia
                role -> role.getListPermissions().remove(objPermission)
        );

        //Después de eliminar los registros de relación en la tabla intermedia, podremos eliminar el permiso
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
