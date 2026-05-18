package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.user.ClientUserRequestDto;
import com.bazar.apibazar.dto.user.UpdateUsernameRequestDto;
import com.bazar.apibazar.dto.user.UserRequestDto;
import com.bazar.apibazar.dto.user.UserResponseDto;
import com.bazar.apibazar.exception.InvalidRoleAssignmentException;
import com.bazar.apibazar.exception.UnauthorizedOperationException;
import com.bazar.apibazar.exception.UserNotFoundException;
import com.bazar.apibazar.exception.UsernameAlreadyExistsException;
import com.bazar.apibazar.model.Cliente;
import com.bazar.apibazar.model.UserSec;
import com.bazar.apibazar.repository.IUserRepository;
import com.bazar.apibazar.security.jwt.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

//Implementamos operaciones de dominio para definir y ejecutar la lógica de dominio
@Service
public class UserService implements IUserService {

    //Inyección de dependencia para el repositorio de usuarios
    private final IUserRepository userRepo;
    //Inyección de dependencia para el contrato del service de Role
    private final IRoleService roleService;
    //Inyección de dependencia para el "hasheador" de contraseñas
    private final BCryptPasswordEncoder passwordEncoder;

    //Inyección de dependencia por constructor
    public UserService(IUserRepository userRepo, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    //Método para construir un DTO para la exposición de un usuario
    private UserResponseDto buildUserResponse(UserSec objUser) {
        //Si el usuario es cliente, sacamos el ID del cliente asociado para agregarlo al DTO de respuesta, si no establecemos el ID como null
        Long clientId = null;
        if(objUser.getCliente() != null){clientId = objUser.getCliente().getIdCliente();}

        // Creamos objeto "UserResponseDto" con los datos de "objUser"
        return new UserResponseDto(objUser.getId(), objUser.getUsername(),
                //Con el método "buildRolesResponse(..) transformamos una lista de roles a ResponseDto para exponerlos"
                roleService.buildRolesResponse(
                        new ArrayList<>(objUser.getListRoles())),
                clientId,
                objUser.isEnabled()
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

    //Método propio para validar si un username asignado a un usuario no existe ya en la bd
    private void validarUsername(String username){
        //Validamos que el username que se quiere asociar al usuario no esté registrado
        if (userRepo.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Ya existe usuario con username: " + username + ", intente con otro");
        }
    }

    //Método para validar que un usuario no haya sido deshabilitado
    private void validarDisponibilidadUser(UserSec user){
        if(!user.isEnabled()){throw new UserNotFoundException("No no encontró usuario con id: " + user.getId());}
    }

    //Método propio para extraer del objeto Authentication en el SecurityContext el id del usuario autenticado
    private Long getAuthenticatedUserId() {

        //Obtenemos el objeto Authentication del SecurityContext con la información del usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Sacamos la identidad del usuario que guardamos como un objeto CustomUserPrincipal, pero dentro de Authentication se guarda con un objeto generalizado tipo Object
        Object objPrincipal = authentication.getPrincipal();

        //Válidamos que el objeto Principal sea instancia de nuestro Principal personalizado "CustomUserPrincipal"
        if(!(objPrincipal instanceof CustomUserPrincipal principal)){throw new UnauthorizedOperationException("No autorizado");}

        return principal.getUserId();
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


    @Override
    public UserSec findByClient(Long clientId) {
        return userRepo.findByCliente_idCliente(clientId)
                .orElseThrow(
                        () -> new UserNotFoundException("No se encontró usuario asociado a cliente con id: " + clientId)
                );
    }

    @Transactional
    @Override
    public UserResponseDto saveUser(UserRequestDto newUser) {

        //Validamos que el username no exista
        validarUsername(newUser.username());

        //En este método no se registran usuarios que son clientes, solo registros administrativos (empleados, administradores, etc)
        if(newUser.rolesNames().contains("CLIENT")){
            throw new InvalidRoleAssignmentException("Los usuarios con el rol CLIENTE deben registrarse a través del flujo de registro de clientes");
        }

        //Creamos objeto que almacena los datos del usuario a persistir
        UserSec objUser = new UserSec();

        //Agregamos los datos del usuario
        //Username
        objUser.setUsername(newUser.username());
        //Guardamos contraseña hasheada con el algoritmo de hash: BCrypt
        objUser.setPassword(passwordEncoder.encode(newUser.password()));
        //Agregamos roles del usuario
        objUser.setListRoles(
                new LinkedHashSet<>(
                        //Buscamos por medio de RoleService los roles que se le quieren asignar al usuario
                        roleService.findAllRolesByNames(new LinkedHashSet<>(newUser.rolesNames()))
                )
        );

        //Persistimos objeto
        UserSec savedUser = userRepo.save(objUser);

        return buildUserResponse(savedUser);
    }

    @Transactional
    @Override
    public UserResponseDto registerClientUser(ClientUserRequestDto clientUserDTO) {
        //Validamos que el username no esté registrado
        validarUsername(clientUserDTO.username());

        //Construimos objeto Cliente para persistirlo junto al usuario
        Cliente newCliente = new Cliente(null,
                clientUserDTO.clientData().nombre(),
                clientUserDTO.clientData().apellido(),
                clientUserDTO.clientData().documento()
        );

        //Construimos objeto UserSec para persistir
        UserSec newClientUser = new UserSec();

        //Agregamos los datos del usuario y le asignamos el cliente construido anteriormente y el rol CLIENTE
        newClientUser.setUsername(clientUserDTO.username());
        newClientUser.setPassword(passwordEncoder.encode(clientUserDTO.password()));
        newClientUser.setCliente(newCliente);
        //Agregamos rol CLIENTE automáticamente
        newClientUser.setListRoles(new LinkedHashSet<>(
                List.of(roleService.findRoleByName("CLIENT"))
        ));

        //Persistimos usuario con el cliente correspondiente
        /* Como en la entidad UserSec definimos CascadeType.PERSIST, Hibernate detectará que el cliente dentro del usuario
           no existe y lo registrará automáticamente en la bd sin necesidad de usar el repo de Cliente */
        userRepo.save(newClientUser);

        return buildUserResponse(newClientUser);
    }

    @Transactional
    @Override
    public void disableUser(Long id) {
        //Buscamos
        UserSec objUser = findUser(id);

        //Deshabilitamos usuario
        objUser.setEnabled(false);
        //Deshabilitamos cliente vinculado a esta cuenta
        objUser.getCliente().setActive(false);

    }

    @Transactional
    @Override
    public UserResponseDto addRolesToUser(Long userId, List<String> newRolesNames) {
        //Buscamos user por si id, en caso de que no exista -> Excepción personalizada
        UserSec user = findUser(userId);

        //Validamos disponibilidad
        validarDisponibilidadUser(user);

        //Buscamos y agregamos los nuevos roles a la lista roles del usuario
        user.getListRoles().addAll(
                roleService.findAllRolesByNames(new LinkedHashSet<>(newRolesNames))
        );

        return buildUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponseDto removeRolesFromUser(Long userId, List<String> removeRolesNames) {
        //Buscamos usuario y confirmamos existencia
        UserSec objUser = findUser(userId);

        //Validamos disponibilidad
        validarDisponibilidadUser(objUser);

        //Validamos que la lista de roles que se quieren eliminar del usuario realmente existen en la BD
        roleService.findAllRolesByNames(new LinkedHashSet<>(removeRolesNames));

        /*Recorremos la lista de Roles del usuario y con el método .removeIf(..) garantizamos que se eliminen, de esa lista,
          de forma segura, los roles que cumplan la condición (el id de estos está dentro de "removeRolesIds")*/
        objUser.getListRoles().removeIf(
                //Usamos función lambda para verificar si el rol en cuestión se mandó a eliminar
                role -> removeRolesNames.contains(role.getName())
        );

        //Retornamos usuario con roles actualizados
        return buildUserResponse(objUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto findMe() {
        //Usamos método para obtener id del usuario autenticado y lo buscamos
        UserSec objUser = findUser(
                getAuthenticatedUserId()
        );

        return buildUserResponse(objUser);
    }

    @Transactional
    @Override
    public UserResponseDto updateUsername(UpdateUsernameRequestDto objUpdateUsername) {

        //Buscamos usuario autenticado
        UserSec objUser = findUser(
                getAuthenticatedUserId()
        );

        //Validamos si las contraseñas coinciden
        boolean verifier = passwordEncoder.matches(
                objUpdateUsername.password(), objUser.getPassword()
        );

        //Si no coinciden las contraseñas, lanzamos excepción de NO Autorizado
        if(!verifier){throw new UnauthorizedOperationException("Contraseña incorrecta");}

        //Si coinciden las contraseñas, actualizamos usuario
        objUser.setUsername(objUpdateUsername.newUsername());

        return buildUserResponse(objUser);
    }


//
//    @Override
//    public void updatePassword(UpdatePasswordRequestDto objUpdatePassword) {
//
//    }
}
