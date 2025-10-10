package com.bazar.apibazar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter  @Setter  
public class ClienteDeVentaDto {
    
    private String nombre;
    private String apellido;
    private String documento;

    public ClienteDeVentaDto(String nombre) {
        this.nombre = nombre;
    }

    public ClienteDeVentaDto(String nombre, String apellido, String documento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
    }
    
    
}
