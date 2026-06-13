package com.bazar.apibazar.bootstrap;


import com.bazar.apibazar.model.Permission;
import com.bazar.apibazar.model.Role;
import com.bazar.apibazar.model.UserSec;
import com.bazar.apibazar.repository.IPermissionRepository;
import com.bazar.apibazar.repository.IRoleRepository;
import com.bazar.apibazar.repository.IUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SystemBootstrap implements CommandLineRunner {

    private final IUserRepository userRepo;
    private final IRoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final IPermissionRepository permissionRepo;

    @Value("${ADMIN_USERNAME:}")
    private String adminUsername;
    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    /**
     * Verifica que las credenciales iniciales
     * del administrador hayan sido configuradas
     * mediante variables de entorno.
     */
    @PostConstruct
    public void validateBootstrapConfig() {

        if (adminUsername == null || adminUsername.isBlank()) {
            throw new IllegalStateException("ADMIN_USERNAME no configurado");
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException("ADMIN_PASSWORD no configurado");
        }
    }

    /**
     * Ejecuta las operaciones que hacen
     * parte del bootstrap al iniciar la aplicación.
     */
    @Transactional
    @Override
    public void run(String... args) {

        createPermissions();
        createRoles();
        createAdmin();
    }

    /**
     * Registra el usuario administrador inicial
     * si aún no existe en el sistema.
     */
    private void createAdmin() {

        if (userRepo.existsByUsername(adminUsername)) {
            return;
        }

        UserSec adminUser = new UserSec();

        adminUser.setUsername(adminUsername);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setListRoles(Set.of(
                roleRepo.findByName("ADMIN")
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Bootstrap inconsistente: rol ADMIN no encontrado"
                                )
                        )
        ));


        userRepo.save(adminUser);
    }

    /**
     * Crea permisos base del sistema para
     * administradores y clientes.
     */
    private void createPermissions() {

        createPermissionIfNotExists("CREATE_PRODUCT");
        createPermissionIfNotExists("UPDATE_PRODUCT");
        createPermissionIfNotExists("DELETE_PRODUCT");
        createPermissionIfNotExists("READ_ROLES");
        createPermissionIfNotExists("CREATE_ROLE");
        createPermissionIfNotExists("ASSIGN_PERMISSION");
        createPermissionIfNotExists("READ_PERMISSIONS");
        createPermissionIfNotExists("CREATE_PERMISSION");
        createPermissionIfNotExists("MANAGE_USERS");
        createPermissionIfNotExists("READ_USERS");

        createPermissionIfNotExists("CREATE_CART");
        createPermissionIfNotExists("CREATE_SALE");
        createPermissionIfNotExists("READ_OWN_PROFILE");
        createPermissionIfNotExists("READ_OWN_CART");
    }

    /**
     * Registra un permiso únicamente
     * si aún no existe.
     */
    private void createPermissionIfNotExists(String name) {
        if (!permissionRepo.existsByName(name)) {
            permissionRepo.save(
                    new Permission(null, name)
            );
        }
    }

    /**
     * Crea roles base del sistema:
     * ADMIN y CLIENT
     */
    private void createRoles() {
        createRoleIfNotExists("ADMIN",
                Set.of(
                        "CREATE_PRODUCT",
                        "UPDATE_PRODUCT",
                        "DELETE_PRODUCT",
                        "READ_ROLES",
                        "CREATE_ROLE",
                        "READ_PERMISSIONS",
                        "CREATE_PERMISSION",
                        "ASSIGN_PERMISSION",
                        "READ_USERS",
                        "MANAGE_USERS"
                )
        );

        createRoleIfNotExists("CLIENT",
                Set.of(
                        "CREATE_CART",
                        "CREATE_SALE",
                        "READ_OWN_PROFILE",
                        "READ_OWN_CART"
                )
        );

    }

    /**
     * Registra un rol únicamente
     * si aún no existe.
     */
    private void createRoleIfNotExists(String name, Set<String> permissions) {
        if (!roleRepo.existsByName(name)) {
            roleRepo.save(
                    new Role(
                            null,
                            name,
                            new LinkedHashSet<>(
                                    permissionRepo.findAllByNameIn(permissions)
                            ),
                            true
                    )
            );
        }
    }
}