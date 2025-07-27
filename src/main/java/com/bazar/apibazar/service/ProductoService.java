package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.ProductoDto;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.repository.IProductoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoService implements IProductoService{
    
    //Inyección de dependecia para ProductoRepository
    @Autowired
    private IProductoRepository productoRepository;
    
    
    @Override
    public List<Producto> getProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto findProducto(Long id) {
        Optional<Producto> objProducto = productoRepository.findById(id);
        
        if(objProducto.isEmpty()){
            return null;
        }
        
        return objProducto.get();
        
    }

    @Override
    public void saveProducto(ProductoDto objNuevo) {
        Producto objProducto = new Producto();
        
        objProducto.setNombre(objNuevo.getNombre());
        objProducto.setMarca(objNuevo.getMarca());
        objProducto.setCosto(objNuevo.getCosto());
        objProducto.setCantidadDisponible(objNuevo.getCantidadDisponible());
        
        productoRepository.save(objProducto);
       
    }

    @Override
    public boolean deleteProducto(Long id) {
        
        if(productoRepository.existsById(id)){
            productoRepository.deleteById(id);
            
            return true;
            
        }
        
        return false;
    }

    @Override
    public Producto updateProducto(Long id, ProductoDto objActualizado) {
        Producto objProducto = findProducto(id);
        
        if(objProducto == null){return objProducto;}
        
        objProducto.setNombre(objActualizado.getNombre());
        objProducto.setMarca(objActualizado.getMarca());
        objProducto.setCosto(objActualizado.getCosto());
        objProducto.setCantidadDisponible(objActualizado.getCantidadDisponible());
        
        productoRepository.save(objProducto);
        
        return objProducto;
        
        
    }

    @Override
    public Producto patchProducto(Long id, ProductoDto objDto) {
        Producto objProducto = findProducto(id);
        
        if(objProducto == null){return objProducto;}
        
        if(objDto.getNombre() != null){objProducto.setNombre(objDto.getNombre());}
        if(objDto.getMarca() != null){objProducto.setMarca(objDto.getMarca());}
        if(objDto.getCosto() != null){objProducto.setCosto(objDto.getCosto());}
        if(objDto.getCantidadDisponible() != null){objProducto.setCantidadDisponible(objDto.getCantidadDisponible());}
        
        productoRepository.save(objProducto);
        
        return objProducto;
    }

    @Override
    public List<Producto> productosPocoStock() {
        
        List<Producto> listProductos = new ArrayList<>();
        
        for(Producto objProducto: getProductos()){
            if(objProducto.getCantidadDisponible() < 5){
                listProductos.add(objProducto);
            }
        }
        
        return listProductos;
    }
    
}
