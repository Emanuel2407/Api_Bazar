package com.bazar.apibazar.security.jwt.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bazar.apibazar.security.jwt.utils.JwtUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

//Clase donde se va a crear el filtro que intercepte la request para validar el token JWT
//OncePerRequestFilter establece que este filtro será ejecutado una sola vez por request
@Component //Registramos instancia de la clase como Bean para que Spring la pueda gestionar
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //Inyección de dependencia para usar las operaciones de la clase de utilidades JWT (JwtUtils)
    private final JwtUtils jwtUtils;
    //Inyección de dependencia por constructor
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    //Sobre-escribimos método para definir filtro personalizado
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {

        //Usamos try-catch{} por si ocurre algún problema en la validación del token
        try {
            //Sacamos, del Header de la Response, el token que normalmente viene con el nombre: AUTHORIZATION
            String token = response.getHeader(HttpHeaders.AUTHORIZATION);

            //Filtramos que el token no sea null y que empiece con el esquema: Bearer
            if (token != null && token.startsWith("Bearer ")) {

                //Como el esquema "Bearer " no forma parte del token, debemos removerlo
                token = token.substring(7);  //'Bearer ': 6 letras + 1 especio = 7 caracteres

                //Intentamos validar el token, en caso de que la validación sea exitosa obtendremos un objeto DecodeJwt con toda la información de este
                DecodedJWT decodedJWT = jwtUtils.validateToken(token);

                //Extraemos username del token
                String username = jwtUtils.extractUsername(decodedJWT);
                //Extraemos Claim de autoridades y las guardamos como String
                String authorities = jwtUtils.findClaim("authorities", decodedJWT).asString();

                //Ahora debemos convertir esa cadena de texto con las autoridades a una colección GrantedAuthority
                List<GrantedAuthority> authoritiesList = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                //Formamos objeto Authentication para setearlo en el contexto de seguridad
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        decodedJWT.getSubject(),
                        null,
                        authoritiesList
                );

                //Limpiamos contexto de seguridad en caso de que otro filtro lo haya usado
                SecurityContextHolder.clearContext();
                //Agregamos el objeto Authentication con los datos del usuario
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            //Si no se encontró filtro o no tiene el Schema: "Bearer ", delegamos al siguiente filtro
            filterChain.doFilter(request, response);

                //Si la autenticación del token falla, capturamos la excepción
        }catch(JWTVerificationException e){

            //Formamos respuesta con StatusCode 401-UNAUTHORIZED
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            //Enviamos mensaje informando el fallo de verificación
            response.getWriter().write("""  
        {
            "error": "Expired or invalid token"
        }
        """);

        //Finalizamos filtro
        return;

        }

    }
}
