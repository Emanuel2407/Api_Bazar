package com.bazar.apibazar.dto.producto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO utilizado al actualizar parcialmente
 * los datos de un producto.
 */
public record ProductoPatchDto(String nombre,
                               String marca,
                               @Positive Double costo,
                               @PositiveOrZero Integer cantidadDisponible) {}