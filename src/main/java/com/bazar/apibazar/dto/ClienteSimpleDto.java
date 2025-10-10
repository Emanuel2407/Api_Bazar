package com.bazar.apibazar.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClienteSimpleDto {
    
    private Long idCliente;
    private String nombre;
    private String apellido;
    private String documento;
    private List<VentaDeClienteDto> listVentas;
    
    public ClienteSimpleDto() {
    }

    public ClienteSimpleDto(Long idCliente, String nombre, String apellido, String documento, List<VentaDeClienteDto> listVentas) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.listVentas = listVentas;
    }

    
    
    
}
