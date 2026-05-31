package com.bazar.apibazar.security.jwt.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bazar.apibazar.security.jwt.CustomUserPrincipal;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Filtro encargado de validar tokens JWT presentes
 * en las peticiones entrantes y establecer la
 * autenticación en el contexto de seguridad.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * Intercepta cada petición HTTP para validar el JWT
     * y registrar la autenticación correspondiente.
     */
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {

        try {
            /* Obtiene el valor del header Authorization
              que puede contener un token JWT */
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (token != null && token.startsWith("Bearer ")) {

                //Remueve esquema "Bearer " para recuperar el token original.
                token = token.substring(7);

                DecodedJWT decodedJWT = jwtUtils.validateToken(token);

                //Construye objeto Authentication y lo guarda en el contexto de seguridad.
                SecurityContextHolder.getContext().setAuthentication(
                        jwtUtils.buildAuthentication(decodedJWT)
                );
            }

            filterChain.doFilter(request, response);

            /*
             * Si el token es inválido o expiró,
             * retorna una respuesta HTTP 401.
             */
        }catch(JWTVerificationException e){

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            response.setContentType("application/json");

            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("""  
        {
            "error": "Expired or invalid token"
        }
        """);

        }

    }
}
