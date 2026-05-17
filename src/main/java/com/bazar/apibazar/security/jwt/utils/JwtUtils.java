package com.bazar.apibazar.security.jwt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bazar.apibazar.model.UserSec;
import com.bazar.apibazar.repository.IUserRepository;
import com.bazar.apibazar.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

//Creamos clase con utilidades JWT donde se ejecutan acciones como crear token JWT, validar token, sacar username del usuario desde el token, etc.
@Component //Registramos instancia de esta clase como Bean para que Spring lo pueda inyectar cuando se necesite
public class JwtUtils {

    //Inyectamos clave secreta con la que se firma el token
    @Value("${security.jwt.secret}")
    private String secretKey;
    //Inyectamos usuario que genera el token
    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    //Inyección de dependencia para repositorio de persistencia de usuarios
    private final IUserRepository userRepo;

    //Inyección de dependencia por método constructor
    public JwtUtils(IUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    //Método para crear el token JWT a partir de los datos del usuario ya autenticado (objeto Authentication en parámetro)
    public String createToken(Authentication authentication){

        //Definimos el algoritmo de firma que vamos a utilizar (en este caso HMAC256)
        //Decodificamos clave secreta y se la pasamos al algoritmo de firma
        Algorithm algorithm = Algorithm.HMAC256(
                Base64.getDecoder().decode(secretKey)
        );

        //Sacamos username del objeto Authentication
        String username = authentication.getName();

        //Buscamos usuario por username
        UserSec user = userRepo.findByUsername(username)
                //En caso de que no se encuentre, lanzamos excepción de SpringSecurity
                .orElseThrow(
                        //Usamos función lambda para retornar excepción indicando que el usuario no es válido
                        () -> new UsernameNotFoundException("Invalid username or password")
                );

        //Si el usuario además es cliente, sacamos su ID correspondiente del objeto cliente relacionado
        Long clienteId=null;
        if(user.getCliente() != null){clienteId=user.getCliente().getIdCliente();}

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
                .withClaim("clientId", clienteId)  //Claim adicional que contiene, en caso de que el usuario sea cliente, su id correspondiente
                .sign(algorithm);  //Finalmente, firmamos el token con el algoritmo de firma definido anteriormente
    }

    //Método para validar la autenticidad y veracidad de un token
    public DecodedJWT validateToken(String token){
        //Definimos algoritmo para reconstruir la firma y validar
        //Como guardamos la clave secreta codificada en Base64, debemos decodificarla para correcta reconstrucción de la firma
        Algorithm algorithm = Algorithm.HMAC256(Base64.getDecoder().decode(secretKey));

        //Definimos verificador de JWT pasándole el algoritmo de firma y la clave secreta
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(userGenerator)  //Le indicamos que además valide la veracidad del usuario que generó el token (Issuer)
                .build();  //Construimos el verificador

        //Con los parámetros anteriores le decimos al verificador que valide el token y devolvemos token decodificado
        return verifier.verify(token);

    }

    //Método para sacar el username del usuario del token decodificado
    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject();  //En el momento de la creación, establecimos el subject como el username del usuario
    }

    //Método para encontrar un Claim es especifico por su nombre
    public Claim findClaim(String claimName, DecodedJWT decodedJWT){
        return decodedJWT.getClaim(claimName);
    }

    //Método para traer todos los claims
    public Map<String, Claim> findAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }

}
