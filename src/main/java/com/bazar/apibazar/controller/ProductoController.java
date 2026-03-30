package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.ProductoDto;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.service.IProductoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

    //Inyección de dependecia para ProductoService
    @Autowired
    IProductoService productoService;

    //Traer todos
    @GetMapping("/")
    public ResponseEntity<List<Producto>> getProductos(){
        return ResponseEntity.ok(productoService.getProductos());
    }

    //Traer uno
    @GetMapping("/{id}")
    public ResponseEntity<Producto> findProducto(@PathVariable Long id){
        return ResponseEntity.ok(productoService.findProducto(id));

    }

    //Productos con stock < 5
    @GetMapping("/falta-stock")
    public ResponseEntity<List<Producto>> productosPocoStock(){
        return ResponseEntity.ok(productoService.productosPocoStock());
    }

    //Ingresamos producto nuevo
    @PostMapping("/")
    public ResponseEntity<Producto> saveProducto(@RequestBody ProductoDto objNuevo){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.saveProducto(objNuevo));
    }

    //Eliminamos producto por id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id){
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    //Actualizamos
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody ProductoDto objActualizado){
        return ResponseEntity.ok(productoService.updateProducto(id, objActualizado));
    }

    //Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<Producto> patchProducto(@PathVariable Long id, @RequestBody ProductoDto objDto){
        return ResponseEntity.ok(productoService.patchProducto(id, objDto));
    }


}
