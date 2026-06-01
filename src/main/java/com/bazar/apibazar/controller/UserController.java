package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.user.*;
import com.bazar.apibazar.service.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService){
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAllUsers(){
        return ResponseEntity.ok(
                userService.findAll()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable Long id){
        return ResponseEntity.ok(
                userService.findUserById(id)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> findMe(){
        return ResponseEntity.ok(
                userService.findMe()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponseDto> registerUserByAdmin(@Valid @RequestBody UserRequestDto newUser){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.saveUser(newUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable Long id){
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/roles")
    public ResponseEntity<UserResponseDto> addRolesToUser(@PathVariable Long userId, @RequestBody @NotEmpty List<@NotBlank String> newRolesNames){
        return ResponseEntity.ok(userService.addRolesToUser(userId, newRolesNames));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}/roles")
    public ResponseEntity<UserResponseDto> removeRoles(@PathVariable Long userId, @RequestBody @NotEmpty List<@NotBlank String> rolesNames){
        return ResponseEntity.ok(userService.removeRolesFromUser(userId, rolesNames));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me/username")
    public ResponseEntity<UserResponseDto> updateUsername(@Valid @RequestBody UpdateUsernameRequestDto objUpdateUsername){
        return ResponseEntity.ok(
                userService.updateUsername(objUpdateUsername)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me/password")
    public ResponseEntity<UserResponseDto> updatePassword(@Valid @RequestBody UpdatePasswordRequestDto objUpdatePassword){
        userService.updatePassword(objUpdatePassword);
        return ResponseEntity.noContent().build();
    }
}
