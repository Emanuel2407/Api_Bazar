package com.bazar.apibazar.repository;

import com.bazar.apibazar.model.UserSec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//Definimos contrato con Spring Data Jpa para tener a los diferentes métodos de persistencia de la interfaz JpaRepository<>
@Repository
public interface IUserRepository extends JpaRepository<UserSec, Long> {

    //Definimos método que Spring Data interpreta (por la sintaxis del nombre) para traer un usuario por su username
    Optional<UserSec> findByUsername(String username);

    //Método que Spring-Data-Jpa interpreta para verificar sin un username está registrado y asociado a algún usuario
    boolean existsByUsername(String username);

    //Definimos método para traer los usuarios que tengan dentro (o asignado) al role cuyo id es el mandado por parámetro
    List<UserSec> findByListRoles_id(Long roleId);

    //Definimos método de consulta personalizada para traer un usuario identificándolo por su cliente asociado
    Optional<UserSec> findByCliente_idCliente(Long clienteId);
}
