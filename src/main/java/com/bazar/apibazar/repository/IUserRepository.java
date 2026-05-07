package com.bazar.apibazar.repository;

import com.bazar.apibazar.model.UserSec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//Definimos contrato con Spring Data Jpa para tener a los diferentes métodos de persistencia de la interfaz JpaRepository<>
@Repository
public interface IUserRepository extends JpaRepository<UserSec, Long> {

    //Definimos método que Spring Data interpreta (por la sintaxis del nombre) para traer un usuario por su username
    Optional<UserSec> findByUsername(String username);
}
