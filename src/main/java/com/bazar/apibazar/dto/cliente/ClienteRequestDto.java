package com.bazar.apibazar.dto.cliente;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

//DTO para recibir los datos de un cliente cuando se va a hacer un registro o una modificación de este
public record ClienteRequestDto(@NotBlank String nombre,
                                @NotBlank String apellido,
                                //Con attern(regexp = "\\d+") aseguramos que el campo sea un String numérico
                                @NotBlank @Pattern(regexp = "\\d+")String documento){}
