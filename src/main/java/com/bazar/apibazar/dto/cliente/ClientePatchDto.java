package com.bazar.apibazar.dto.cliente;

import jakarta.validation.constraints.Pattern;

/**
 * DTO utilizado cuando se solicita la
 * actualización parcial de los datos de un cliente.
 */
public record ClientePatchDto(String nombre,
                              String apellido,
                              //Con @Pattern(regexp = \\d+") aseguramos que el campo documento sea un String numérico
                              @Pattern(regexp = "\\d+") String documento){}