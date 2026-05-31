package com.bazar.apibazar.security.jwt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bazar.apibazar.model.UserSec;
import com.bazar.apibazar.repository.IUserRepository;
import com.bazar.apibazar.security.jwt.CustomUserPrincipal;
import com.bazar.apibazar.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Operaciones para gestionar el token JWT y
 * guardar el usuario autenticado en el contexto de seguridad.
 */
@Component
public class JwtUtils {

    //Inyecta clave secreta para firmar el token.
    @Value("${security.jwt.secret}")
    private String secretKey;

    //Inyecta el identificador del emisor (issuer) del token.
    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    private final IUserRepository userRepo;

    public JwtUtils(IUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Crea un token JWT a partir de los datos del
     * usuario autenticado.
     */
    public String createToken(Authentication authentication){

        /*Define el algoritmo de firma usado para firmar el JWT con
           la clave secreta asignada*/
        Algorithm algorithm = Algorithm.HMAC256(
                Base64.getDecoder().decode(secretKey)
        );

        String username = authentication.getName();

        /*Busca usuario por nombre de usuario o
        * lanza UsernameNotFoundException si no existe.
        */
        UserSec user = userRepo.findByUsername(username)
                .orElseThrow(

                        () -> new UsernameNotFoundException("Invalid username or password")
                );

        //Si el usuario es cliente, se agrega su id al JWT
        Long clienteId=null;
        if(user.getCliente() != null){clienteId=user.getCliente().getIdCliente();}

        //Expone la lista de autoridades del usuario como lista de String
        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();


        Instant now = Instant.now();

        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(username)
                .withIssuer(userGenerator)
                //Usa java.time para definir fecha de entrega y expiración del token
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(
                        now.plus(Duration.ofMinutes(30)))
                )
                .withClaim("authorities", authorities)
                .withClaim("clientId", clienteId)
                .withClaim("userId", user.getId())
                .sign(algorithm);
    }


    /**
     * Valida autenticidad del token JWT y devuelve un objeto DecodedJWT
     * con la información verificada.
     */
    public DecodedJWT validateToken(String token){
        //Algoritmo usado para reconstruir la firma y poder validar
        Algorithm algorithm = Algorithm.HMAC256(Base64.getDecoder().decode(secretKey));

        JWTVerifier verifier = JWT.require(algorithm)
                //Configura el verificador para que valide el usuario que generó el token.
                .withIssuer(userGenerator)
                .build();

        return verifier.verify(token);

    }

    /**
     * Recupera el nombre de usuario almacenado
     * en el subject del token.
     */
    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject();
    }

    /**
     * Busca un claim específico dentro del token
     */
    public Claim findClaim(String claimName, DecodedJWT decodedJWT){
        return decodedJWT.getClaim(claimName);
    }

    /**
     * Retorna todos los claims contenidos en el JWT
     */
    public Map<String, Claim> findAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }

    /**
     * Construye objeto autenticación para guardar
     * información del JWT en el contexto de seguridad
     */
    public Authentication buildAuthentication(DecodedJWT decodedJWT) {

        String username = this.extractUsername(decodedJWT);

        List<String> authorities = this.findClaim("authorities", decodedJWT).asList(String.class);

        /*Construye autoridades del usuario para
        * guardarlas en el Authentication.
        */
        Collection<? extends GrantedAuthority> authoritiesList =
                authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        /* Recupera identidad de negocio del usuario o guarda null
         * si el usuario no es cliente.*/
        Long clientId = this.findClaim("clientId", decodedJWT).asLong();

        /* Recupera identificador del usuario
         * auténticado.*/
        Long userId = this.findClaim("userId", decodedJWT).asLong();

        //Construye Principal personalizado.
        CustomUserPrincipal principal = new CustomUserPrincipal(userId, clientId, username);

        return  new UsernamePasswordAuthenticationToken(
                principal,
                null,
                authoritiesList
        );
    }

}
