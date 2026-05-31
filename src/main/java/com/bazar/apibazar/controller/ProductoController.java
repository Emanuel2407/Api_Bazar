package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.producto.ProductoPatchDto;
import com.bazar.apibazar.dto.producto.ProductoRequestDto;
import com.bazar.apibazar.dto.producto.ProductoResponseDto;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.service.IProductoService;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final IProductoService productoService;

    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> getProductos(){
        return ResponseEntity.ok(productoService.getProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> findProducto(@PathVariable Long id){
        return ResponseEntity.ok(productoService.findProductoResponse(id));

    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponseDto>> productosPocoStock(){
        return ResponseEntity.ok(productoService.productosPocoStock());
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDto> createProducto(@Valid @RequestBody ProductoRequestDto objNuevo){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.saveProducto(objNuevo));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableProducto(@PathVariable Long id){
        productoService.disableProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> updateProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDto objActualizado){
        return ResponseEntity.ok(productoService.updateProducto(id, objActualizado));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> patchProducto(@PathVariable Long id, @Valid @RequestBody ProductoPatchDto objDto){
        return ResponseEntity.ok(productoService.patchProducto(id, objDto));
    }

}
