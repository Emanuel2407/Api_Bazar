package com.bazar.apibazar.dto;

//Esta clase ayudará a agregarle Ventas a un Cliente por medio de los IDs de estas
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class ClienteAgregarVentasDto {
    
    List<Long> ventasIds = new ArrayList<>();

    public ClienteAgregarVentasDto() {
    }    
    
    public ClienteAgregarVentasDto(List<Long> ventasIds) {
        this.ventasIds = ventasIds;
    } 
}
