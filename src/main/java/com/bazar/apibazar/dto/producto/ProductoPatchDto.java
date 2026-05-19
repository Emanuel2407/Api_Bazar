package com.bazar.apibazar.dto.producto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

//DTO para transportar los datos de un producto cuando se haga una actualización parcial de este
public record ProductoPatchDto(String nombre,
                               String marca,
                               @Positive Double costo,
                               @PositiveOrZero Integer cantidadDisponible) {}