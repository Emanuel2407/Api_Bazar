package com.bazar.apibazar.security.config;

import com.bazar.apibazar.security.jwt.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    /**
     * Define la cadena de filtros de Spring Security
     * que procesa cada petición HTTP entrante.
     */
    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity
                .csrf(csrf -> csrf.disable())

                //Activa BasicAuth como mecanismo adicional de autenticación
                .httpBasic(Customizer.withDefaults())

                // Configura autenticación sin estado (STATELESS)
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define las reglas de autorización para cada petición.
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/auth/**").permitAll()
                            .requestMatchers(GET, "/productos").permitAll()
                            .requestMatchers(GET, "/productos/{id}").permitAll()
                            .anyRequest().authenticated();
                })

                // Agrega el filtro JWT antes del filtro de autenticación básica.
                .addFilterBefore(jwtFilter, BasicAuthenticationFilter.class)

                .build();
    }

    /**
     * Expone el AuthenticationManager encargado de coordinar
     * los proveedores de autenticación configurados.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Registra mecanismo de hash
     * para mantener la confidencialidad al
     * guardar las contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Registra un proveedor de autenticación basado en DAO
     * que obtiene los datos del usuario mediante UserDetailsService
     * y valida la contraseña utilizando PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

}
