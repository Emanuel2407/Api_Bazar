package com.bazar.apibazar.repository;

import com.bazar.apibazar.model.VentaProducto;
import com.bazar.apibazar.model.VentaProductoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVentaProductoRepository extends JpaRepository<VentaProducto, VentaProductoId>{
    
}
