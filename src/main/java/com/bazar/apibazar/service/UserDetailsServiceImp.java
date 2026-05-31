package com.bazar.apibazar.service;

import com.bazar.apibazar.model.UserSec;
import com.bazar.apibazar.repository.IUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de UserDetailsService encargada de consultar
 * usuarios desde la base de datos para el proceso de autenticación.
 */
@Service
public class UserDetailsServiceImp implements UserDetailsService {

    private final IUserRepository userRepo;
    public UserDetailsServiceImp(IUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Busca usuario por username y retorna sus
     * datos de autenticación.
     * */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Consulta usuario o lanza UsernameNotFoundException si no existe
        UserSec user = userRepo.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Invalid username or password")
                );

        List<GrantedAuthority> authorities = new ArrayList<>();

        /* Convierte cada rol del usuario en una autoridad
          con el prefijo ROLE_ requerido por Spring Security */
        user.getListRoles().forEach(
                role -> authorities.add(
                        new SimpleGrantedAuthority("ROLE_".concat(role.getName()))
                )
        );

        //Convierte cada permiso en una autoridad del usuario
        user.getListRoles()
                .stream()
                .flatMap(role -> role.getListPermissions().stream())
                .forEach(
                        permission -> authorities.add(new SimpleGrantedAuthority(permission.getName()))
                );

        // Construye el UserDetails que utilizará Spring Security
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNotExpired(),
                user.isCredentialNotExpired(),
                user.isAccountNotLocked(),
                authorities
        );
    }
}
