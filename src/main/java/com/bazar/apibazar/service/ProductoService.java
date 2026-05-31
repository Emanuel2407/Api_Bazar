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

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductoService implements IProductoService{

    private final IProductoRepository productoRepository;
    public ProductoService(IProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void validarStockProductos(List<VentaProductoDto> productosValidarStock){

        for(VentaProductoDto productoValidar: productosValidarStock){

            //Busca el registro de cada producto solicitado
            Producto objProducto = findProducto(
                    productoValidar.getProductoId()
            );

            if(objProducto.getCantidadDisponible() < productoValidar.getCantidad()){
                throw new ProductoStockInsuficienteException(
                    "El producto con id: " + productoValidar.getProductoId() + " no tiene stock suficiente para cubrir la cantidad: " + productoValidar.getCantidad()
                );
            }
        }
    }

    /**
     * Construye DTO de respuesta para exponer un producto.
     * */
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

        List<ProductoResponseDto> productosResponse = new ArrayList<>();

        for(Producto objProducto: productoRepository.findByAvailableTrue()){
            productosResponse.add(buildProductoResponse(
                    objProducto
            ));
        }

        return productosResponse;
    }

    /**
     * Busca producto por su id y retorna excepción de dominio
     * si este no existe o está deshabilitado para vender.
     */
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
    @Override
    public void saveProducto(Producto objNuevo) {productoRepository.save(objNuevo);}

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

        return buildProductoResponse(objProducto);


    }

    @Transactional
    @Override
    public ProductoResponseDto patchProducto(Long id, ProductoPatchDto objDto) {
        Producto objProducto = findProducto(id);

        if(objDto.nombre() != null){

            /*Si se manda a cambiar el nombre, validamos que no sea por una cadena de
              texto vacía o llena de espacios*/
            if(objDto.nombre().isBlank()){throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre no puede estar vacío"
            );}

            objProducto.setNombre(objDto.nombre());
        }

        if(objDto.marca() != null){

            if(objDto.marca().isBlank()){throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La marca no puede estar vacía"
            );}

            objProducto.setMarca(objDto.marca());
        }

        if(objDto.costo() != null){objProducto.setCosto(objDto.costo());}
        if(objDto.cantidadDisponible() != null){objProducto.setCantidadDisponible(objDto.cantidadDisponible());}

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
