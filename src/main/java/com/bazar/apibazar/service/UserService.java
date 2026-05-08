package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.user.UserRequestDto;
import com.bazar.apibazar.dto.user.UserResponseDto;
import com.bazar.apibazar.exception.UserNotFoundException;
import com.bazar.apibazar.exception.UsernameAlreadyExistsException;
import com.bazar.apibazar.model.UserSec;
import com.bazar.apibazar.repository.IUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

//Implementamos operaciones de dominio para definir y ejecutar la lógica de dominio
@Service
public class UserService implements IUserService {

    //Inyección de dependencia para el repositorio de usuarios
    private final IUserRepository userRepo;
    //Inyección de dependencia para el contrato del service de Role
    private final IRoleService roleService;

    //Inyección de dependencia por constructor
    public UserService(IUserRepository userRepo, RoleService roleService) {
        this.userRepo = userRepo;
        this.roleService = roleService;
    }

    //Método para construir un DTO para la exposición de un usuario
    private UserResponseDto buildUserResponse(UserSec objUser) {
        //Creamos objeto "UserResponseDto" con los datos de "objUser"
        return new UserResponseDto(objUser.getId(), objUser.getUsername(),
                //Con el método "buildRolesResponse(..) transformamos una lista de roles a ResponseDto para exponerlos"
                roleService.buildRolesResponse(
                        new ArrayList<>(objUser.getListRoles()))
        );
    }

    //Método para construir una lista de DTO de respuesta a partir de los datos de una lista de usuarios
    private List<UserResponseDto> buildUsersResponse(List<UserSec> listUsers) {
        //Lista de Dto de usuarios
        List<UserResponseDto> usersResponse = new ArrayList<>();

        //Recorremos la lista de usuarios y usamos una función lambda para transformar cada usuario en su respectivo DTO de respuesta
        listUsers.forEach(
                user -> usersResponse.add(buildUserResponse(user))
        );

        //Retornamos lista de Dto
        return usersResponse;
    }

    //Método para consultar los datos de un usuario y en caso de que no exista -> Excepción personalizada
    private UserSec findUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("No se encontró usuario con Id: " + id)
                );
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> findAll() {
        return buildUsersResponse(userRepo.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto findUserById(Long id) {
        return buildUserResponse(
                findUser(id)
        );
    }

    @Transactional
    @Override
    public UserResponseDto saveUser(UserRequestDto newUser) {
        //Validamos que el username que se quiere asociar al usuario no esté registrado
        if (userRepo.existsByUsername(newUser.username())) {
            throw new UsernameAlreadyExistsException("Ya existe usuario con username: " + newUser.username() + ", intente con otro");
        }

        //Creamos objeto que almacena los datos del usuario a persistir
        UserSec objUser = new UserSec();

        //Agregamos los datos del usuario
        //Username
        objUser.setUsername(newUser.username());
        //Guardamos contraseña hasheada con el algoritmo de hash: BCrypt
        objUser.setPassword(new BCryptPasswordEncoder().encode(newUser.password()));
        //Agregamos roles del usuario
        objUser.setListRoles(
                new LinkedHashSet<>(
                        //Buscamos por medio de RoleService los roles que se le quieren asignar al usuario
                        roleService.findAllRolesById(newUser.rolesIds())
                )
        );

        //Persistimos objeto
        UserSec savedUser = userRepo.save(objUser);

        return buildUserResponse(savedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        //Buscamos y eliminamos usuario (si existe)
        userRepo.delete(
                findUser(id)
        );
    }

    @Transactional
    @Override
    public UserResponseDto addRolesToUser(Long userId, List<Long> newRolesIds) {
        //Buscamos user por si id, en caso de que no exista -> Excepción personalizada
        UserSec user = findUser(userId);

        //Buscamos y agregamos los nuevos roles a la lista roles del usuario
        user.getListRoles().addAll(
                roleService.findAllRolesById(newRolesIds)
        );

        return buildUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponseDto removeRolesFromUser(Long userId, List<Long> removeRolesIds) {
        //Buscamos usuario y confirmamos existencia
        UserSec objUser = findUser(userId);

        //Validamos que la lista de roles que se quieren eliminar del usuario realmente existen en la BD
        roleService.findAllRolesById(removeRolesIds);

        /*Recorremos la lista de Roles del usuario y con el método .removeIf(..) garantizamos que se eliminen, de esa lista,
          de forma segura, los roles que cumplan la condición (el id de estos está dentro de "removeRolesIds")*/
        objUser.getListRoles().removeIf(
                //Usamos función lambda para verificar si el rol en cuestión se mandó a eliminar
                role -> removeRolesIds.contains(role.getId())
        );

        //Retornamos usuario con roles actualizados
        return buildUserResponse(objUser);
    }


//    @Override
//    public UserResponseDto updateUsername(UpdateUsernameRequestDto objUpdateUser) {
//        return null;
//    }
//
//    @Override
//    public void updatePassword(UpdatePasswordRequestDto objUpdatePassword) {
//
//    }
}
