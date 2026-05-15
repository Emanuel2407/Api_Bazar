package com.bazar.apibazar.repository;

import com.bazar.apibazar.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

//Definimos contrato con Spring Data Jpa para tener a los diferentes métodos de persistencia de la interfaz JpaRepository<>
@Repository
public interface IPermissionRepository extends JpaRepository<Permission, Long> {

    //Definimos método de consulta personalizada que Spring-Data-Jpa interpreta para traer, consultando por sus nombres, una lista de permisos
    List<Permission> findAllByNameIn(Set<String> permissionsNames);
}
