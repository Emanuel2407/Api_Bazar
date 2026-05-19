package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.user.*;
import com.bazar.apibazar.service.IUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Controlador para recibir las request de componente Users
@RestController
@RequestMapping("/users")
public class UserController {

    //Inyección de dependencia para UserService
    private final IUserService userService;
    //Inyección de dependencia por constructor
    public UserController(IUserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAllUsers(){
        return ResponseEntity.ok(
                userService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable Long id){
        return ResponseEntity.ok(
                userService.findUserById(id)
        );
    }

    //Endpoint para ingresar al método que consulta los datos del objeto autenticado guardado en el SecurityContext
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> findMe(){
        return ResponseEntity.ok(
                userService.findMe()
        );
    }

    //Registro administrativo de usuarios
    @PostMapping
    public ResponseEntity<UserResponseDto> saveUser(@Valid @RequestBody UserRequestDto newUser){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.saveUser(newUser));
    }

    //Endpoint de registro público de usuarios
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> saveClientUser(@Valid @RequestBody ClientUserRequestDto newClientUser){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerClientUser(newClientUser));
    }

    //Soft Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> disableUser(@PathVariable Long id){
        userService.disableUser(id);
        return ResponseEntity.noContent().build();
    }

    //Agregar roles al usuario
    @PostMapping("/{userId}/add-roles")
    public ResponseEntity<UserResponseDto> addRolesToUser(@PathVariable Long userId, @RequestBody @NotEmpty List<@NotBlank String> newRolesNames){
        return ResponseEntity.ok(userService.addRolesToUser(userId, newRolesNames));
    }

    //Eliminar roles de un usuario
    @DeleteMapping("/{userId}/remove-roles")
    public ResponseEntity<UserResponseDto> removeRoles(@PathVariable Long userId, @RequestBody @NotEmpty List<@NotBlank String> rolesNames){
        return ResponseEntity.ok(userService.removeRolesFromUser(userId, rolesNames));
    }

    //Endpoint para acceder al método de actualizar username de usuario autenticado
    @PostMapping("/update-username")
    public ResponseEntity<UserResponseDto> updateUsername(@Valid @RequestBody UpdateUsernameRequestDto objUpdateUsername){
        return ResponseEntity.ok(
                userService.updateUsername(objUpdateUsername)
        );
    }

    //Endpoint para acceder al método de actualizar password de usuario autenticado
    @PostMapping("/update-password")
    public ResponseEntity<UserResponseDto> updatePassword(@Valid @RequestBody UpdatePasswordRequestDto objUpdatePassword){
        userService.updatePassword(objUpdatePassword);
        return ResponseEntity.ok().build();
    }
}
