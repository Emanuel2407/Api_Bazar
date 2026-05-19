package com.bazar.apibazar.dto.venta;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record VentaRequestDto(
        @NotEmpty List<@Valid VentaProductoDto> listProductos){}