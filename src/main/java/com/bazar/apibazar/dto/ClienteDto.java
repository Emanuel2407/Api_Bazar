package com.bazar.apibazar.dto;

import lombok.Getter;
import lombok.Setter;

@Setter  @Getter
public class ClienteDto {
    
    private String nombre;
    private String apellido;
    private String documento;

    public ClienteDto() {
    }

    public ClienteDto(String nombre, String apellido, String documento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
    }
    
    
}
