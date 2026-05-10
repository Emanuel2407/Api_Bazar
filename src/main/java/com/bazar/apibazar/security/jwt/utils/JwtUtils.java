package com.bazar.apibazar.security.jwt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

//Creamos clase con utilidades JWT donde se ejecutan acciones como crear token JWT, validar token, sacar username del usuario desde el token, etc.
public class JwtUtils {

    //Inyectamos clave secreta con la que se firma el token
    @Value("${security.jwt.seceret}")
    private String secretKey;
    //Inyectamos usuario que genera el token
    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    //Método para crear el token JWT a partir de los datos del usuario ya autenticado (objeto Authentication en parámetro)
    public String createToken(Authentication authentication){

        //Definimos el algoritmo de firma que vamos a utilizar (en este caso HMAC256)
        //Decodificamos clave secreta y se la pasamos al algoritmo de firma
        Algorithm algorithm = Algorithm.HMAC256(
                Base64.getDecoder().decode(secretKey)
        );

        //Sacamos username del objeto Authentication
        String username = authentication.getPrincipal().toString();

        //Sacamos autoridades del usuario y las convertimos a cadena de texto con las autoridades separadas por coma
        String authorities = authentication.getAuthorities()
                //Usamos .stream() para habilitar métodos especiales para trabajar con colecciones
                .stream()
                //.map(..) transforma cada objeto GrantedAuthority de la colección a lo retornado por su método getAuthority() (Nombre de la autoridad)
                .map(GrantedAuthority::getAuthority)
                //Construimos la cadena de texto con los elementos resultantes de la colección separados por coma
                .collect(Collectors.joining(","));

        //Guardamos fecha actual para, con base a esta, definir el tiempo de vida que tendrá el token una vez creado
        Instant now = Instant.now();

        return JWT.create() //Llamamos la builder de Java-Jwt
                //Datos que irán en el payload:
                .withJWTId(UUID.randomUUID().toString())  //Generamos ID random y lo asignamos al token
                .withSubject(username) //Agregamos username
                .withIssuer(userGenerator)  //Agregamos usuario que genera el token
                .withIssuedAt(Date.from(now))  //Agregamos fecha en que se emite el token
                .withExpiresAt(Date.from(  //Agregamos fecha de expiración del token
                        now.plus(Duration.ofMinutes(30)))  //Usamos java.time para adicionar 30 minutos a partir de la creación del token
                )
                .withClaim("authorities", authorities)  //Claim adicional que contiene las autoridades del usuario
                .sign(algorithm);  //Finalmente, firmamos el token con el algoritmo de firma definido anteriormente
    }
}
