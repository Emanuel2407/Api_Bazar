package com.bazar.apibazar.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  //Definimos que en esta clase se registran Beans de configuración
@EnableWebSecurity  //Activamos la integración de la aplicación con Spring Security
public class SecurityConfig {

    @Bean  //Bean que registra la cadena de filtros de seguridad que interceptan cada request
    public SecurityFilterChain securityFilter(HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity
                // Se desactiva CSRF (común en APIs REST que no usan cookies/sesiones)
                .csrf(csrf -> csrf.disable())
                // Configurar la gestión de sesiones como Stateless
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //Autorización de peticiones
                .authorizeHttpRequests(auth -> {
                    //Inicialmente, se define que todas las request serán públicas
                    auth.anyRequest().permitAll();
                })
                //Construcción de la cadena de filtros de seguridad con base a las reglas anteriores
                .build();
    }

    @Bean  //Registramos Bean con authenticationManager que es el encargado de orquestar los diferentes AuthenticationProviders (mecanismos de autenticación) que puedan haber para autenticar al usuario
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        //Sacamos el authenticationManager otorgado por AuthenticationConfiguration
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean //Registramos Bean con mecanismo de hash para la contraseña del usuario
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
