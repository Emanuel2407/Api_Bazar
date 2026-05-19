package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.producto.ProductoPatchDto;
import com.bazar.apibazar.dto.producto.ProductoRequestDto;
import com.bazar.apibazar.dto.producto.ProductoResponseDto;
import com.bazar.apibazar.dto.venta.VentaProductoDto;
import com.bazar.apibazar.exception.ProductoNotFoundException;
import com.bazar.apibazar.exception.ProductoStockInsuficienteException;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.repository.IProductoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService implements IProductoService{

    //Inyección de dependencia para ProductoRepository
    private final IProductoRepository productoRepository;
    //Inyección de dependencia por constructor
    public ProductoService(IProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    //Método propio para validar si el stock de una lista de productos es suficiente para cubrir una cierta cantidad
    public void validarStockProductos(List<VentaProductoDto> productosValidarStock){

        //Recorremos la lista para validar uno por uno los productos
        for(VentaProductoDto productoValidar: productosValidarStock){
            //Buscamos producto, si no existe -> Excepción de dominio
            Producto objProducto = findProducto(productoValidar.getProductoId());

            //Si el stock es insuficiente -> Excepción de dominio
            if(objProducto.getCantidadDisponible() < productoValidar.getCantidad()){
                throw new ProductoStockInsuficienteException(
                    "El producto con id: " + productoValidar.getProductoId() + " no tiene stock suficiente para cubrir la cantidad: " + productoValidar.getCantidad()
                );
            }
        } //Si llega el final del bucle y no hay excepciones -> Los productos son válidos para vender
    }

    //Método propio para construir un DTO que se usa para exponer un producto
    private ProductoResponseDto buildProductoResponse(Producto objProducto){
        return new ProductoResponseDto(
                objProducto.getIdProducto(),
                objProducto.getNombre(),
                objProducto.getMarca(),
                objProducto.getCosto(),
                objProducto.getCantidadDisponible(),
                objProducto.isAvailable()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductoResponseDto> getProductos() {
        //Productos a exponer
        List<ProductoResponseDto> productosResponse = new ArrayList<>();

        //Construimos DTO de productos a exponer
        for(Producto objProducto: productoRepository.findByAvailableTrue()){
            productosResponse.add(buildProductoResponse(
                    objProducto
            ));
        }

        //Exponemos solo los productos habilitados para vender
        return productosResponse;
    }

    @Override
    public Producto findProducto(Long id) {
        Optional<Producto> objProducto = productoRepository.findById(id);

        if(objProducto.isEmpty() || !objProducto.get().isAvailable()){throw new ProductoNotFoundException("No se encontró producto con id: " + id);}

        return objProducto.get();

    }

    @Transactional(readOnly = true)
    @Override
    public ProductoResponseDto findProductoResponse(Long id) {
        return buildProductoResponse(
                findProducto(id)
        );

    }

    @Transactional
    @Override
    public ProductoResponseDto saveProducto(ProductoRequestDto objNuevo) {
        Producto objProducto = new Producto();

        objProducto.setNombre(objNuevo.nombre());
        objProducto.setMarca(objNuevo.marca());
        objProducto.setCosto(objNuevo.costo());
        objProducto.setCantidadDisponible(objNuevo.cantidadDisponible());

        productoRepository.save(objProducto);

        return buildProductoResponse(objProducto);

    }

    @Transactional
    //Sobre-carga del método saveProducto que será útil en venta-service para registrar un producto desde allá
    public void saveProducto(Producto objNuevo) {productoRepository.save(objNuevo);}

    //Método usado por venta-service para registrar una lista de productos
    @Transactional
    public void saveAll(List<Producto> listProducto) {productoRepository.saveAll(listProducto);}

    @Transactional
    @Override
    public void disableProducto(Long id) {
        Producto objProducto = findProducto(id);

        objProducto.setAvailable(false);
    }

    @Transactional
    @Override
    public ProductoResponseDto updateProducto(Long id, ProductoRequestDto objActualizado) {
        Producto objProducto = findProducto(id);

        objProducto.setNombre(objActualizado.nombre());
        objProducto.setMarca(objActualizado.marca());
        objProducto.setCosto(objActualizado.costo());
        objProducto.setCantidadDisponible(objActualizado.cantidadDisponible());

        productoRepository.save(objProducto);

        return buildProductoResponse(objProducto);


    }

    @Transactional
    @Override
    public ProductoResponseDto patchProducto(Long id, ProductoPatchDto objDto) {
        Producto objProducto = findProducto(id);

        if(objDto.nombre() != null){objProducto.setNombre(objDto.nombre());}
        if(objDto.marca() != null){objProducto.setMarca(objDto.marca());}
        if(objDto.costo() != null){objProducto.setCosto(objDto.costo());}
        if(objDto.cantidadDisponible() != null){objProducto.setCantidadDisponible(objDto.cantidadDisponible());}

        productoRepository.save(objProducto);

        return buildProductoResponse(objProducto);
    }

    @Transactional
    @Override
    public List<ProductoResponseDto> productosPocoStock() {

        List<ProductoResponseDto> listProductos = new ArrayList<>();

        for(Producto objProducto: productoRepository.findAll()){
            if(objProducto.getCantidadDisponible() < 5){
                listProductos.add(buildProductoResponse(
                        objProducto)
                );
            }
        }

        return listProductos;
    }





}
