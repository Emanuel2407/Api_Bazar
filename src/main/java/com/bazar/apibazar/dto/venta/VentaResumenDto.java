package com.bazar.apibazar.dto.venta;

public record VentaResumenDto(
    
     Long idVenta,
     Double total,
     Integer cantProductos,
     String nombreCliente,
     String apellidoCliente){
}
