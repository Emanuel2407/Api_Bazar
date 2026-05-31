package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.user.UserLoginRequestDto;
import com.bazar.apibazar.dto.user.UserLoginResponseDto;
import com.bazar.apibazar.security.jwt.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthService(JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Login que autentica al usuario usando las credenciales de username y password
     * */
    public UserLoginResponseDto login(UserLoginRequestDto userLogin) {

        Authentication authentication = authenticationManager.authenticate(
                //Indica al AuthenticationManager que autentique por username y password
                new UsernamePasswordAuthenticationToken(
                        userLogin.username(),
                        userLogin.password()
                )
        );

        String tokenJwt = jwtUtils.createToken(authentication);

        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new UserLoginResponseDto(
                userLogin.username(),
                tokenJwt,
                authorities
        );
    }
}
