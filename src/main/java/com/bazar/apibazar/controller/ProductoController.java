package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.ProductoDto;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.service.IProductoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    //Inyección de dependecia para ProductoService
    @Autowired
    IProductoService productoService;

    //Traer todos
    @GetMapping("/")
    @ResponseBody
    public List<Producto> getProductos(){
        return productoService.getProductos();
    }

    //Traer uno
    @GetMapping("/{id}")
    public ResponseEntity<?> findProducto(@PathVariable Long id){
        return ResponseEntity.ok(productoService.findProducto(id));

    }

    //Productos con stock < 5
    @GetMapping("/falta-stock")
    public List<Producto> productosPocoStock(){
        return productoService.productosPocoStock();
    }

    //Ingresamos
    @PostMapping("/")
    public void saveProducto(@RequestBody ProductoDto objNuevo){productoService.saveProducto(objNuevo);}

    //Eliminamos
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id){
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    //Actualizamos
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducto(@PathVariable Long id, @RequestBody ProductoDto objActualizado){
        return ResponseEntity.ok(productoService.updateProducto(id, objActualizado));
    }

    //Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchProducto(@PathVariable Long id, @RequestBody ProductoDto objDto){
        return ResponseEntity.ok(productoService.patchProducto(id, objDto));
    }


}
