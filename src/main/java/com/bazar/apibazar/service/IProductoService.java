package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.ProductoDto;
import com.bazar.apibazar.model.Producto;
import java.util.List;

public interface IProductoService {
    
    List<Producto> getProductos();
    
    Producto findProducto(Long id);
    
    void saveProducto(ProductoDto objNuevo);
    
    void saveProducto(Producto objNuevo);
    
    //Método para guardar varios Productos almacenados en una lista 
    void saveAll(List<Producto> listProducto);
    
    boolean deleteProducto(Long id);
    
    Producto updateProducto(Long id, ProductoDto objActualizado);
    
    Producto patchProducto(Long id, ProductoDto objDto);
    
    //Método para obtener todos los productos cuya cantidadDisponible sea menor a 5
    List<Producto> productosPocoStock();
}
