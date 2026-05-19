package com.bazar.apibazar.dto.cliente;

import jakarta.validation.constraints.Pattern;

//DTO para recibir los datos de un cliente cuando se va a hacer una modificación parcial de este
public record ClientePatchDto(String nombre,
                              String apellido,
                              //Con @Pattern(regexp = \\d+") aseguramos que el campo documento sea un String numérico
                              @Pattern(regexp = "\\d+") String documento){}