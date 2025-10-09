package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.GetVentaDto;
import com.bazar.apibazar.dto.VentaDto;
import com.bazar.apibazar.dto.VentaProductoDto;
import com.bazar.apibazar.dto.VentaResumenDto;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.model.Venta;
import java.time.LocalDate;
import java.util.List;

public interface IVentaService {
    
    /*Como queremos que este método nos devuelva sólo la lista de productos que estan relacionados con la 
    respectiva Venta y no la lista completa de la tabla intermedia VentaProducto. Entonces para lograsr esto
    hacemos la implementacion de una clase Dto que devuelva lo mismo que la clase Venta normal excepto que la
    lista de VentaProducto se va a cambiar por una lista de productos normalita*/
    List<GetVentaDto> getVentasSimples();
    
    //Se devuelve objeto Dto con una lista de simples productos
    GetVentaDto findVentaSimple(Long id);
    
    //Método get normalito
    List<Venta> getVentas();
    
    //Método find normalito
    Venta findVenta(Long id);
    
    void saveVenta(VentaDto objNuevo);
    
    boolean deleteVenta(Long id);
    
    Venta updateVenta(Long id, VentaDto objActualizado);
    
    Venta patchVenta(Long id, VentaDto objDto);
    
    //Método para agregar una cierta cantidad de productos nuevos a una venta sin afectar los que la venta ya tenía    
    Venta addProductosAVenta(Long id, List<VentaProductoDto> productosNuevos);
            
    //Obtener la lista de productos de una determinada venta
    List<Producto> productosDeVenta(Long id);
    
    //Obteber el monto total y la cantidad de ventas que se hicieron en una determinada fecha
    String  ventasDelDia(LocalDate fechaVenta);
    
    //Obtener la venta con mayor monto total usando el objeto DTO VentaResumenDto
    VentaResumenDto findMayorVenta();
}
