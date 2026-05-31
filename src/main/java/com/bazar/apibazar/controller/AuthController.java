package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.user.ClientUserRequestDto;
import com.bazar.apibazar.dto.user.UserLoginRequestDto;
import com.bazar.apibazar.dto.user.UserLoginResponseDto;
import com.bazar.apibazar.dto.user.UserResponseDto;
import com.bazar.apibazar.service.AuthService;
import com.bazar.apibazar.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final IUserService userService;

    public AuthController(AuthService authService, IUserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody @Valid UserLoginRequestDto userLogin){
        return ResponseEntity.ok(authService.login(userLogin));
    }

    //Registro público de clientes.
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerClient(@Valid @RequestBody ClientUserRequestDto newClientUser){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerClientUser(newClientUser));
    }
}
