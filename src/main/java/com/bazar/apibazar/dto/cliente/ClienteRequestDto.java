package com.bazar.apibazar.dto.cliente;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClienteRequestDto(@NotBlank String nombre,
                                @NotBlank String apellido,
                                //Con attern(regexp = "\\d+") aseguramos que el campo sea un String numérico
                                @NotBlank @Pattern(regexp = "\\d+")String documento){}
