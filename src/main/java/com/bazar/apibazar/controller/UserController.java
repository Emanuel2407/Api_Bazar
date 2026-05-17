package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.user.ClientUserRequestDto;
import com.bazar.apibazar.dto.user.UserRequestDto;
import com.bazar.apibazar.dto.user.UserResponseDto;
import com.bazar.apibazar.service.IUserService;
import jakarta.validation.Valid;
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
    public ResponseEntity<UserResponseDto> addRolesToUser(@PathVariable Long userId, @RequestBody List<String> newRolesNames){
        return ResponseEntity.ok(userService.addRolesToUser(userId, newRolesNames));
    }

    //Eliminar roles de un usuario
    @DeleteMapping("/{userId}/remove-roles")
    public ResponseEntity<UserResponseDto> removeRoles(@PathVariable Long userId, @RequestBody List<String> rolesNames){
        return ResponseEntity.ok(userService.removeRolesFromUser(userId, rolesNames));
    }
}
