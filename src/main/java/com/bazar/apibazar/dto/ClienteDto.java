package com.bazar.apibazar.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter  @Getter
public class ClienteDto {
    
    private String nombre;
    private String apellido;
    private String documento;
    private List<VentaDto> listVentas = new ArrayList<>();
    
    public ClienteDto() {
    }

    public ClienteDto(String nombre, String apellido, String documento, List<VentaDto> listVentas) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.listVentas = listVentas;
    }

    
    
    
}
