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

//Servicio de login para autenticar al usuario
@Service
public class AuthService {

    //Inyección de dependencia para el AuthenticationManager que gestiona los mecanismos de autenticación que implementamos
    private final AuthenticationManager authenticationManager;
    //Le indicamos a Spring que inyecte el Bean con la clase de utilidades JWT (JwtUtils)
    private final JwtUtils jwtUtils;
    //Inyección de dependencia por constructor
    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils){
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    //Método que se encargará del proceso de login (autenticar al usuario, generar el token de acceso y devolverlo al cliente)
    public UserLoginResponseDto login(UserLoginRequestDto userLogin){

        /*Delegamos la autenticación al AuthenticationManager proporcionándole los datos que envía el cliente (usuario y contraseña)
           para que se autentique al usuario usando el AuthenticationProvider correspondiente */
        //Si el usuario se logra autenticar correctamente, se retorna objeto Authentication con los datos de este
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userLogin.username(),
                userLogin.password()
        ));

        //Generamos token JWT con base al usuario autenticado
        String tokenJwt = jwtUtils.createToken(authentication);

        //Convertimos la lista de GrantedAuthority a lista de String con los nombres de las autoridades
        List<String> authorities = authentication.getAuthorities()
                .stream()
                //Transformamos cada objeto GrantedAuthority a lo que retorne el método getAuthority() (Nombre de la autoridad)
                .map(GrantedAuthority::getAuthority)
                .toList();

        //Finalmente, construimos DTO de respuesta con los datos del usuario que se autenticó y el token JWT
        return new UserLoginResponseDto(authentication.getName(),
                tokenJwt,
                authorities);
    }
}
