package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.user.UserRequestDto;
import com.bazar.apibazar.dto.user.UserResponseDto;
import com.bazar.apibazar.service.IUserService;
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

    @PostMapping
    public ResponseEntity<UserResponseDto> saveUser(@RequestBody UserRequestDto newUser){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.saveUser(newUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    //Agregar roles al usuario
    @PostMapping("/{userId}/add-roles")
    public ResponseEntity<UserResponseDto> addRolesToUser(@PathVariable Long userId, @RequestBody List<Long> newRolesIds){
        return ResponseEntity.ok(userService.addRolesToUser(userId, newRolesIds));
    }

    //Eliminar roles de un usuario
    @DeleteMapping("/{userId}/remove-roles")
    public ResponseEntity<UserResponseDto> removeRoles(@PathVariable Long userId, @RequestBody List<Long> rolesIds){
        return ResponseEntity.ok(userService.removeRolesFromUser(userId, rolesIds));
    }
}
