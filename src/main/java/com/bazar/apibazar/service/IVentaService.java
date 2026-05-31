package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.venta.*;
import com.bazar.apibazar.model.Producto;

import java.time.LocalDate;
import java.util.List;

public interface IVentaService {

    /**
     * Devuelve una venta con objetos DTO
     * que representan los productos comprados.
     */
    List<VentaResponseDto> getVentasSimples();

    /**
     * Devuelve una lista de ventas con objetos DTO
     * que representa los productos comprados.
     */
    VentaResponseDto findVentaSimple(Long id);
    
    VentaResponseDto saveVenta(List<VentaProductoDto> listProductos);
    
    void cancelVenta(Long id);
    
    /**
     * Agrega productos nuevos a una venta.
     * Si se agregan productos que ya hacían parte
     * de la venta solo se le suma la cantidad comprada.
     */
    VentaResponseDto addProductosAVenta(Long id, List<VentaProductoDto> productosNuevos);
            
    /**
     * Elimina productos de una venta
     */
    VentaResponseDto deleteProductosDeVenta(Long id, List<VentaProductoDto> productosEliminados);
    
    /**
     * Consulta los productos dentro de una venta.
     */
    List<Producto> productosDeVenta(Long id);

    /**
     * Obtener el monto total y la cantidad de ventas que se hicieron en una determinada fecha.
     */
    String  ventasDelDia(LocalDate fechaVenta);
    
    /**
     * Consulta la mayor venta registrada.
     */
    VentaResumenDto findMayorVenta();
   
}
