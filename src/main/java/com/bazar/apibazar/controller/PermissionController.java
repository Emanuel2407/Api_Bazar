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

    private final IPermissionService permissionService;

    public PermissionController(IPermissionService permissionService){
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponseDto>> findAllPermissions(){
        return ResponseEntity.ok(permissionService.findAllPermissions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponseDto> findPermissionById(@PathVariable Long id){
        return ResponseEntity.ok(permissionService.findPermissionById(id));
    }

    @PostMapping
    public ResponseEntity<PermissionResponseDto> savePermission(@Valid @RequestBody PermissionRequestDto newPermission){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(permissionService.savePermission(newPermission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id){
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponseDto> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionRequestDto updatedPermission){
        return ResponseEntity.ok(permissionService.updatePermission(id, updatedPermission));
    }
}
