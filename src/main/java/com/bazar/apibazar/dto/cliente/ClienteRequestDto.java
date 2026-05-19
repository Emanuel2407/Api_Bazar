package com.bazar.apibazar.dto.cliente;


import jakarta.validation.constraints.NotBlank;

//DTO para recibir los datos de un cliente cuando se va a hacer un registro o una modificación de este
public record ClienteRequestDto(@NotBlank String nombre,
                                @NotBlank String apellido,
                                @NotBlank String documento){}
