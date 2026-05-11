package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.user.UserLoginRequestDto;
import com.bazar.apibazar.dto.user.UserLoginResponseDto;
import com.bazar.apibazar.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    //Inyección de dependencia para login en AuthenticationService
    private final AuthService authService;
    //Inyección de dependencia por constructor
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //Definimos end-point para login
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody @Valid UserLoginRequestDto userLogin){
        return ResponseEntity.ok(authService.login(userLogin));
    }
}
