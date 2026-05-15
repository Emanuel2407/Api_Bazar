package com.bazar.apibazar.repository;

import com.bazar.apibazar.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

//Definimos repositorio de la entidad Role que hereda métodos de la interfaz JpaRepository para la persistencia y gestión de roles
@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    //Definimos método que Spring-Data-Jpa interpreta para traer una lista de roles que tengan un permiso cuyo id sea el mandado por parámetro
    List<Role> findByListPermissions_id(Long permissionId);

    //Definimos método de consulta personalizado para consultar los datos de un grupo de roles consultando por sus nombres
    List<Role> findAllByNameIn(Set<String> rolesNames);
}
