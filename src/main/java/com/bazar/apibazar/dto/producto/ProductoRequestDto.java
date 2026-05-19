package com.bazar.apibazar.dto.producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductoRequestDto(@NotBlank String nombre,
                                 @NotBlank String marca,
                                 @NotNull @Positive Double costo,
                                 @NotNull @PositiveOrZero Integer cantidadDisponible) {}
