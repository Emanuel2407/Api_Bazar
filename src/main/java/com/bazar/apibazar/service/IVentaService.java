package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.VentaDto;
import com.bazar.apibazar.dto.VentaProductoDto;
import com.bazar.apibazar.dto.VentaResumenDto;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.model.Venta;
import java.time.LocalDate;
import java.util.List;

public interface IVentaService {
    List<Venta> getVentas();
    
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
