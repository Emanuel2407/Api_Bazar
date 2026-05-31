package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.producto.ProductoPatchDto;
import com.bazar.apibazar.dto.producto.ProductoRequestDto;
import com.bazar.apibazar.dto.producto.ProductoResponseDto;
import com.bazar.apibazar.dto.venta.VentaProductoDto;
import com.bazar.apibazar.model.Producto;
import java.util.List;

public interface IProductoService {
    
    List<ProductoResponseDto> getProductos();
    
    Producto findProducto(Long id);

    ProductoResponseDto findProductoResponse(Long id);

    ProductoResponseDto saveProducto(ProductoRequestDto objNuevo);

    /**
     * Registro de productos consumido por venta-service
     */
    void saveProducto(Producto objNuevo);

    void disableProducto(Long id);
    
    ProductoResponseDto updateProducto(Long id, ProductoRequestDto objActualizado);
    
    ProductoResponseDto patchProducto(Long id, ProductoPatchDto objDto);
    
    //Método para obtener todos los productos cuya cantidadDisponible sea menor a 5
    List<ProductoResponseDto> productosPocoStock();

    /**
     * Valida el stock de una lista de productos
     * que se desean comprar.
     * */
    void validarStockProductos(List<VentaProductoDto> productosValidarStock);
}
