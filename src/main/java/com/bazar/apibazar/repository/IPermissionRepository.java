package com.bazar.apibazar.repository;

import com.bazar.apibazar.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Definimos contrato con Spring Data Jpa para tener a los diferentes métodos de persistencia de la interfaz JpaRepository<>
@Repository
public interface IPermissionRepository extends JpaRepository<Permission, Long> {
}
