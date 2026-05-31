package com.bazar.apibazar.dto.venta;

/**
 * DTO para mostrar los detalles
 * de la venta de un cliente.
 * */
public record VentaResumenDto(
    
     Long idVenta,
     Double total,
     Integer cantProductos,
     String nombreCliente,
     String apellidoCliente){
}
