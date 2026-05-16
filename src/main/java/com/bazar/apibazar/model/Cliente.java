package com.bazar.apibazar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter  @Setter
public class Cliente {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idCliente;
    private String nombre;
    private String apellido;
    private String documento;
    //Implementación de SoftDelete, los clientes se deshabilitan en lugar de borrarse para preservar la trazabilidad e integridad de los datos de estos
    private boolean active=true; //Por defecto habilitado

    /*La relación con "venta" será unidireccional del lado de venta, por lo que el usuario no conoce sus ventas cuando se hace
       el registro de este (porque normalmente cuando un cliente se registra no se le asignan ventas de una)*/

    public Cliente() {
    }

    public Cliente(Long idCliente, String nombre, String apellido, String documento) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
    }
    
    
}
