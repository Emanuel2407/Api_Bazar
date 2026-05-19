package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.permission.PermissionRequestDto;
import com.bazar.apibazar.dto.permission.PermissionResponseDto;
import com.bazar.apibazar.service.IPermissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    //Inyección de dependencia para el contrato (Interfaz que define operaciones públicas) de PermissionService
    private final IPermissionService permissionService;
    //Inyección de dependencia por constructor
    public PermissionController(IPermissionService permissionService){
        this.permissionService = permissionService;
    }

    //End-point para traer todos los permisos
    @GetMapping
    public ResponseEntity<List<PermissionResponseDto>> findAllPermissions(){
        return ResponseEntity.ok(permissionService.findAllPermissions());
    }

    //End-point para consultar un permiso por su id
    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponseDto> findPermissionById(@PathVariable Long id){
        return ResponseEntity.ok(permissionService.findPermissionById(id));
    }

    //End-point para registrar un nuevo permiso
    @PostMapping
    public ResponseEntity<PermissionResponseDto> savePermission(@Valid @RequestBody PermissionRequestDto newPermission){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(permissionService.savePermission(newPermission));
    }

    //End-point para eliminar un permiso por su id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id){
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    //End-point para actualizar un determinado permiso
    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponseDto> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionRequestDto updatedPermission){
        return ResponseEntity.ok(permissionService.updatePermission(id, updatedPermission));
    }
}
