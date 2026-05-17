package com.bazar.apibazar.repository;

import com.bazar.apibazar.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Long>{

    //Consulta personalizada para traer de la base de datos solo los productos que estén disponibles para vender (available=true)
    List<Producto> findByAvailableTrue();
}
