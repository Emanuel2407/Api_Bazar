package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.user.*;
import com.bazar.apibazar.exception.InvalidRoleAssignmentException;
import com.bazar.apibazar.exception.UnauthorizedOperationException;
import com.bazar.apibazar.exception.UserNotFoundException;
import com.bazar.apibazar.exception.UsernameAlreadyExistsException;
import com.bazar.apibazar.model.Cliente;
import com.bazar.apibazar.model.Role;
import com.bazar.apibazar.model.UserSec;
import com.bazar.apibazar.repository.IUserRepository;
import com.bazar.apibazar.security.jwt.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

//Implementamos operaciones de dominio para definir y ejecutar la lógica de dominio
@Service
public class UserService implements IUserService {

    private final IUserRepository userRepo;
    private final IRoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(IUserRepository userRepo, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Construye DTO de respuesta usado para exponer usuarios
     */
    private UserResponseDto buildUserResponse(UserSec objUser) {
        Long clientId = null;
        if(objUser.getCliente() != null){clientId = objUser.getCliente().getIdCliente();}

        return new UserResponseDto(objUser.getId(), objUser.getUsername(),
                //Construye DTO de roles
                roleService.buildRolesResponse(
                        new ArrayList<>(objUser.getListRoles())),
                clientId,
                objUser.isEnabled()
        );
    }

    /**
     * Construye una lista de DTOs de respuesta de usuarios
     */
    private List<UserResponseDto> buildUsersResponse(List<UserSec> listUsers) {
        List<UserResponseDto> usersResponse = new ArrayList<>();

        listUsers.forEach(
                user -> usersResponse.add(
                        buildUserResponse(user)
                )
        );

        //Retornamos lista de Dto
        return usersResponse;
    }

    /**
     * Consulta un usuario por su id o lanza excepción
     * de dominio si no existe.
     * */
    private UserSec findUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("No se encontró usuario con Id: " + id)
                );
    }

    /**
     * Valida que no se duplique un nombre de usuario en los registros.
     */
    private void validarUsername(String username){

        if (userRepo.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Ya existe usuario con username: " + username + ", intente con otro");
        }
    }

    /**
     * Valida si un usuario está activo.
     * */
    private void validarDisponibilidadUser(UserSec user){
        if(!user.isEnabled()){throw new UserNotFoundException("No se encontró usuario con id: " + user.getId());}
    }

    /**
     * Recupera identidad del usuario del Security Context y retorna su Id
     */
    private Long getAuthenticatedUserId() {
        //Obtiene la autenticación actual del usuario.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Saca objeto Principal con la identidad del usuario.
        Object objPrincipal = authentication.getPrincipal();

        //Válida que el objeto Principal sea instancia de nuestro Principal personalizado "CustomUserPrincipal"
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
        validarUsername(newUser.username());

        //El registro de clientes se hace por el flujo del registro público
        if(newUser.rolesNames().contains("CLIENT")){
            throw new InvalidRoleAssignmentException("Los usuarios con el rol CLIENTE deben registrarse a través del flujo de registro de clientes");
        }

        UserSec objUser = new UserSec();

        objUser.setUsername(newUser.username());

        objUser.setPassword(passwordEncoder.encode(newUser.password()));

        //Consulta roles en RoleService
        List<Role> listRoles = roleService.findAllRolesByNames(
                new LinkedHashSet<>(newUser.rolesNames())
        );

        roleService.validarEstadoDeRoles(listRoles);

        objUser.setListRoles(
                new LinkedHashSet<>(
                        listRoles
                )
        );

        UserSec savedUser = userRepo.save(objUser);

        return buildUserResponse(savedUser);
    }

    @Transactional
    @Override
    public UserResponseDto registerClientUser(ClientUserRequestDto clientUserDTO) {
        validarUsername(clientUserDTO.username());

        //Identidad comercial del usuario
        Cliente newCliente = new Cliente(
                null,
                clientUserDTO.clientData().nombre(),
                clientUserDTO.clientData().apellido(),
                clientUserDTO.clientData().documento(),
                true
        );

        //Identidad de autenticación del usuario
        UserSec newClientUser = new UserSec();

        newClientUser.setUsername(clientUserDTO.username());

        newClientUser.setPassword(
                passwordEncoder.encode(clientUserDTO.password())
        );

        newClientUser.setCliente(newCliente);

        //Cataloga al usuario como cliente
        Role clientRole = roleService.findRoleByName("CLIENT");

        roleService.validarEstadoDeRoles(List.of(clientRole));

        newClientUser.setListRoles(new LinkedHashSet<>(
                List.of(clientRole))
        );

        //Aplica operación cascada y persiste al cliente con el usuario.
        userRepo.save(newClientUser);

        return buildUserResponse(newClientUser);
    }

    @Transactional
    @Override
    public void disableUser(Long id) {
        UserSec objUser = findUser(id);

        //Si el usuario es cliente debemos deshabilitar su identidad de negocio
        if(objUser.getCliente() != null){
            objUser.getCliente().setActive(false);
        }

        objUser.setEnabled(false);


    }

    @Transactional
    @Override
    public UserResponseDto addRolesToUser(Long userId, List<String> newRolesNames) {

        UserSec user = findUser(userId);

        validarDisponibilidadUser(user);

        /*No se asigna rol "CLIENT" por este flujo,
           solo en registro público*/
        if (newRolesNames.contains("CLIENT")) {
            throw new InvalidRoleAssignmentException("Los usuarios con rol 'CLIENT' deben registrarse a través del flujo de registro correspondiente");
        }

        List<Role> listRoles = roleService.findAllRolesByNames(
                new LinkedHashSet<>(newRolesNames)
        );

        //Verifica que los nuevos roles asignados al usuario estén activos
        roleService.validarEstadoDeRoles(listRoles);

        user.getListRoles().addAll(
                listRoles
        );

        return buildUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponseDto removeRolesFromUser(Long userId, List<String> removeRolesNames) {

        UserSec objUser = findUser(userId);

        validarDisponibilidadUser(objUser);

        /*No se elimina el rol "CLIENT" de los usuarios,
          ya que esto puede causar inconsistencia en la autenticación de clientes*/
        if(removeRolesNames.contains("CLIENT")){
            throw new InvalidRoleAssignmentException("Los usuarios con rol 'CLIENT' deben deshabilitarse por el flujo de deshabilitación de usuarios");
        }

        //Valida existencia de roles
        roleService.findAllRolesByNames(
                new LinkedHashSet<>(removeRolesNames)
        );

        objUser.getListRoles().removeIf(
                role -> removeRolesNames.contains(role.getName())
        );

        return buildUserResponse(objUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto findMe() {
        //Consulta datos de usuario autenticado
        UserSec objUser = findUser(
                getAuthenticatedUserId()
        );

        return buildUserResponse(objUser);
    }

    @Transactional
    @Override
    public UserResponseDto updateUsername(UpdateUsernameRequestDto objUpdateUsername) {

        UserSec objUser = findUser(
                getAuthenticatedUserId()
        );

        //Valida autenticidad de contraseña
        boolean verifier = passwordEncoder.matches(
                objUpdateUsername.password(), objUser.getPassword()
        );

        if(!verifier){throw new UnauthorizedOperationException("Contraseña incorrecta");}

        objUser.setUsername(objUpdateUsername.newUsername());

        return buildUserResponse(objUser);
    }

    @Transactional
    @Override
    public void updatePassword(UpdatePasswordRequestDto objUpdatePassword) {

        UserSec user = findUser(
                getAuthenticatedUserId()
        );

        //Validamos autenticidad de contraseña
        boolean verifier = passwordEncoder.matches(
                objUpdatePassword.currentPassword(), user.getPassword()
        );

        if(!verifier){throw new UnauthorizedOperationException("Contraseña incorrecta");}

        user.setPassword(
                passwordEncoder.encode(
                       objUpdatePassword.newPassword()
                )
        );

    }
}
