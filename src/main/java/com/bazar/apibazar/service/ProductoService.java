package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.ProductoDto;
import com.bazar.apibazar.exception.ProductoNotFoundException;
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

        if(objProducto.isEmpty()){throw new ProductoNotFoundException("No se encontró producto con id: " + id);}

        return objProducto.get();

    }

    @Override
    public Producto saveProducto(ProductoDto objNuevo) {
        Producto objProducto = new Producto();

        objProducto.setNombre(objNuevo.getNombre());
        objProducto.setMarca(objNuevo.getMarca());
        objProducto.setCosto(objNuevo.getCosto());
        objProducto.setCantidadDisponible(objNuevo.getCantidadDisponible());

        productoRepository.save(objProducto);

        return objProducto;

    }

    //Sobrecarg del método saveProducto que será útil en venta-service para registrar un producto desde allá
    public void saveProducto(Producto objNuevo) {productoRepository.save(objNuevo);}

    //Método usado por venta-service para registrar una lista de productos
    public void saveAll(List<Producto> listProducto) {productoRepository.saveAll(listProducto);}

    @Override
    public void deleteProducto(Long id) {
        Producto objProducto = findProducto(id);
        productoRepository.delete(objProducto);
    }

    @Override
    public Producto updateProducto(Long id, ProductoDto objActualizado) {
        Producto objProducto = findProducto(id);

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
