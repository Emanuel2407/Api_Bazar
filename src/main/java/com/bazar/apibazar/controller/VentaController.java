package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.venta.*;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.service.IVentaService;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("/ventas")
public class VentaController {
    
    //Inyección de dependencia para VentaService
    private final IVentaService ventaService;
    //Inyección de dependencia por constructor
    public VentaController(IVentaService ventaService) {
        this.ventaService = ventaService;
    }

    //Traer todos
    @GetMapping
    public ResponseEntity<List<VentaResponseDto>> getVentas(){
        return ResponseEntity.ok(ventaService.getVentasSimples());
    }
    
    //Traer uno
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDto> findVenta(@PathVariable Long id){
        return ResponseEntity.ok(ventaService.findVentaSimple(id));
    }
    
    //Traer productos de una venta
    @GetMapping("/productos/{id}")
    public ResponseEntity<List<Producto>> productosDeVenta(@PathVariable Long id){
        return ResponseEntity.ok(ventaService.productosDeVenta(id));
    }
    
    //Traer el monto total y la cantidad de ventas de un determinado día
    @GetMapping("/fecha/{fechaVenta}")
    public ResponseEntity<String> ventasDelDia(@PathVariable @NotNull LocalDate fechaVenta){
        return ResponseEntity.ok(ventaService.ventasDelDia(fechaVenta));
    }
    
    @GetMapping("/mayor-venta")
    public ResponseEntity<VentaResumenDto> findMayorVenta(){
        return ResponseEntity.ok(ventaService.findMayorVenta());
        
    }
    
    //Ingresamos venta
    @PostMapping
    public ResponseEntity<VentaResponseDto> saveVenta(@RequestBody @NotEmpty List<@Valid VentaProductoDto> listProductos){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaService.saveVenta(listProductos));
    }
    
    //Endpoint para cancelar venta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelVenta(@PathVariable Long id){
        ventaService.cancelVenta(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    //Agregar productos a Venta existente
    @PostMapping("/agregar-productos/{id}")
    public ResponseEntity<VentaResponseDto> addProductosAventa(@PathVariable Long id, @RequestBody @NotEmpty List<@Valid VentaProductoDto> productosNuevos) {
        return ResponseEntity.ok(ventaService.addProductosAVenta(id, productosNuevos));
    }
    
    //Eliminar productos de Venta existente
    @DeleteMapping("/eliminar-productos/{id}")
    ResponseEntity<?> eliminarProductosDeVenta(@PathVariable Long id, @RequestBody @NotEmpty List<@Valid VentaProductoDto> productosEliminados){
        return ResponseEntity.ok(ventaService.deleteProductosDeVenta(id, productosEliminados));
    }
}
