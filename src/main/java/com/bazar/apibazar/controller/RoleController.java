package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.role.RoleRequestDto;
import com.bazar.apibazar.dto.role.RoleResponseDto;
import com.bazar.apibazar.service.IRoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    //Inyección de dependencia para el contrato (Interfaz que define operaciones públicas) de PermissionService
    private final IRoleService roleService;

    //Inyección de dependencia por constructor
    public RoleController(IRoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> findAllRoles() {
        return ResponseEntity.ok(roleService.findAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> findRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.findRoleById(id));
    }

    @PostMapping
    public ResponseEntity<RoleResponseDto> saveRole(@Valid @RequestBody RoleRequestDto newRole) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.saveRole(newRole));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> disableRole(@PathVariable Long id) {
        roleService.disableRole(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequestDto updatedRole) {
        return ResponseEntity.ok(roleService.updateRole(id, updatedRole));
    }

    //Definimos end-point para agregar una lista de permisos dentro de un rol
    @PostMapping("/{idRole}/add-permissions")
    public ResponseEntity<RoleResponseDto> addPermissionToRole(@PathVariable Long idRole, @RequestBody @NotEmpty List<@NotBlank String> newPermissionsNames){
        return ResponseEntity.ok(roleService.addPermissionsToRole(idRole, newPermissionsNames));
    }

    //End-point para eliminar una lista de permisos dentro de un rol
    @DeleteMapping("/{idRole}/delete-permissions")
    public ResponseEntity<RoleResponseDto> deletePermissionsFromRole(@PathVariable Long idRole, @RequestBody @NotEmpty List<@NotBlank String> removePermissionsNames){
        return ResponseEntity.ok(
                roleService.removePermissionsFromRole(idRole, removePermissionsNames)
        );
    }
}
