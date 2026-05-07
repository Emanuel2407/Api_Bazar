package com.bazar.apibazar.repository;

import com.bazar.apibazar.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Definimos repositorio de la entidad Role que hereda métodos de la interfaz JpaRepository para la persistencia y gestión de roles
@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {
}
