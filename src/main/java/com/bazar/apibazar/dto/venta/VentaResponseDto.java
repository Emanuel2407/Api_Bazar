package com.bazar.apibazar.dto.venta;

import java.time.LocalDate;
import java.util.List;

import com.bazar.apibazar.dto.cliente.ClienteSimpleDto;
import com.bazar.apibazar.model.VentaStatus;

public record VentaResponseDto (
     Long idVenta,
     LocalDate fechaVenta,
     Double totalVenta,
     Integer cantidadTotalProductos,
     List<ProductoDeVentaDto> listProductos,
     ClienteSimpleDto cliente,
     VentaStatus status){}

