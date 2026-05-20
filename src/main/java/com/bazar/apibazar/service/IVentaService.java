package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.venta.*;
import com.bazar.apibazar.model.Producto;

import java.time.LocalDate;
import java.util.List;

public interface IVentaService {
    
    /*Como queremos que este método nos devuelva sólo la lista de productos que estan relacionados con la 
    respectiva Venta y no la lista completa de la tabla intermedia VentaProducto. Entonces para lograsr esto
    hacemos la implementacion de una clase Dto que devuelva lo mismo que la clase Venta normal excepto que la
    lista de VentaProducto se va a cambiar por una lista de productos normalita*/
    List<VentaResponseDto> getVentasSimples();
    
    //Se devuelve objeto Dto con una lista de simples productos
    VentaResponseDto findVentaSimple(Long id);
    
    VentaResponseDto saveVenta(List<VentaProductoDto> listProductos);
    
    void cancelVenta(Long id);
    
    //Método para agregar una cierta cantidad de productos nuevos a una venta sin afectar los que la venta ya tenía    
    VentaResponseDto addProductosAVenta(Long id, List<VentaProductoDto> productosNuevos);
            
    //Método para eliminarle una cierta cantidad  de productos a una venta
    VentaResponseDto deleteProductosDeVenta(Long id, List<VentaProductoDto> productosEliminados);
    
    //Obtener la lista de productos de una determinada venta
    List<Producto> productosDeVenta(Long id);
    
    //Obtener el monto total y la cantidad de ventas que se hicieron en una determinada fecha
    String  ventasDelDia(LocalDate fechaVenta);
    
    //Obtener la venta con mayor monto total usando el objeto DTO VentaResumenDto
    VentaResumenDto findMayorVenta();
   
}
