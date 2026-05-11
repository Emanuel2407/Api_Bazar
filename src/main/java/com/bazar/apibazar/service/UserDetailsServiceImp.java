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

//Implementación de UserDetailsService para buscar y traer los datos de un determinado usuario desde una base de datos
@Service
public class UserDetailsServiceImp implements UserDetailsService {

    //Inyección de dependencia para el repositorio de usuario
    private final IUserRepository userRepo;
    //Inyección de dependencia por constructor
    public UserDetailsServiceImp(IUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override  //Se sobreescribe método loadUserByUsername encargado de leer al usuario por username y retornar los datos de este
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Buscamos usuario por username
        UserSec user = userRepo.findByUsername(username)
                //En caso de que no se encuentre, lanzamos excepción de SpringSecurity
                .orElseThrow(
                        //Usamos función lambda para retornar excepción indicando que el usuario no es válido
                        () -> new UsernameNotFoundException("Invalid username or password")
                );

        //Creamos colección con objetos GrantedAuthority para almacenar las autoridades (roles y permisos) del usuario
        List<GrantedAuthority> authorities = new ArrayList<>();

        //Recorremos lista de roles del usuario para almacenar cada uno en la lista de autoridades (authorities)
        user.getListRoles().forEach(
                //Usamos función lambda para crear y agregar objeto SimpleGrantedAuthority (implementación de GrantedAuthority) a partir del nombre de cada rol
                role -> authorities.add((new SimpleGrantedAuthority("ROLE_".concat(role.getName()))))
        );

        //Agregamos los permisos (que están dentro de cada rol) a la lista de autoridades (authorities)
        user.getListRoles()
                //Usamos .stream() para habilitar métodos especiales para trabajar con colecciones (como .fatMap())
                .stream()
                //.flatMap() "aplana" en una sola lista todas las listas de objetos Permission que están dentro de cada rol
                .flatMap(role -> role.getListPermissions().stream())
                //Iteramos sobre esa mega-lista de permisos para agregar cada uno de estos a la lista de autoridades
                .forEach(
                        //Función lambda para agregar a "authorities" el objeto SimpleAuthority creado a partir del nombre de cada permiso
                        permission -> authorities.add(new SimpleGrantedAuthority(permission.getName()))
                );

        //Finalmente, retornamos los detalles del usuario dentro de un objeto User (implementación de UserDetails)
        return new User(
                user.getUsername(), user.getPassword(),  authorities
        );
    }
}
