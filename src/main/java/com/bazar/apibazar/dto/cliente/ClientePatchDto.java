package com.bazar.apibazar.dto.cliente;

import jakarta.validation.constraints.NotBlank;

//DTO para recibir los datos de un cliente cuando se va a hacer una modificación parcial de este
public record ClientePatchDto(String nombre,
                              String apellido,
                              String documento){}