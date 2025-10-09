package com.bazar.apibazar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
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
    //Relación 1 a n con ventas 
    @OneToMany
    @JoinColumn(name= "cliente_Id", referencedColumnName= "idCliente")
    private List<Venta> listVentas = new ArrayList<>();

    public Cliente() {
    }

    public Cliente(Long idCliente, String nombre, String apellido, String documento) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
    }
    
    
}
