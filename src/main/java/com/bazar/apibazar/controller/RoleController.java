package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.role.RoleRequestDto;
import com.bazar.apibazar.dto.role.RoleResponseDto;
import com.bazar.apibazar.service.IRoleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final IRoleService roleService;

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
    public ResponseEntity<RoleResponseDto> createRole(@Valid @RequestBody RoleRequestDto newRole) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.saveRole(newRole));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableRole(@PathVariable Long id) {
        roleService.disableRole(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDto> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequestDto updatedRole) {
        return ResponseEntity.ok(roleService.updateRole(id, updatedRole));
    }

    @PostMapping("/{idRole}/permissions")
    public ResponseEntity<RoleResponseDto> addPermissionToRole(@PathVariable Long idRole, @RequestBody @NotEmpty List<@NotBlank String> newPermissionsNames){
        return ResponseEntity.ok(roleService.addPermissionsToRole(idRole, newPermissionsNames));
    }

    @DeleteMapping("/{idRole}/permissions")
    public ResponseEntity<RoleResponseDto> deletePermissionsFromRole(@PathVariable Long idRole, @RequestBody @NotEmpty List<@NotBlank String> removePermissionsNames){
        return ResponseEntity.ok(
                roleService.removePermissionsFromRole(idRole, removePermissionsNames)
        );
    }
}
