package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.VentaSimpleDto;
import com.bazar.apibazar.dto.VentaDto;
import com.bazar.apibazar.dto.VentaProductoDto;
import com.bazar.apibazar.dto.VentaResumenDto;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.service.IVentaService;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ventas")
public class VentaController {
    
    //Inyección de dependencia para VentaService
    @Autowired
    IVentaService ventaService; 
    
    //Traer todos
    @GetMapping("/")
    public ResponseEntity<List<VentaSimpleDto>> getVentas(){
        return ResponseEntity.ok(ventaService.getVentasSimples());
    }
    
    //Traer uno
    @GetMapping("/{id}")
    public ResponseEntity<VentaSimpleDto> findVenta(@PathVariable Long id){
        return ResponseEntity.ok(ventaService.findVentaSimple(id));
    }
    
    //Traer productos de una venta
    @GetMapping("/productos/{id}")
    public ResponseEntity<List<Producto>> productosDeVenta(@PathVariable Long id){
        return ResponseEntity.ok(ventaService.productosDeVenta(id));
    }
    
    //Traer el monto total y la cantidad de ventas de un determinado día
    @GetMapping("/fecha/{fechaVenta}")
    public ResponseEntity<String> ventasDelDia(@PathVariable LocalDate fechaVenta){
        return ResponseEntity.ok(ventaService.ventasDelDia(fechaVenta));
    }
    
    @GetMapping("/mayor-venta")
    public ResponseEntity<VentaResumenDto> findMayorVenta(){
        return ResponseEntity.ok(ventaService.findMayorVenta());
        
    }
    
    //Ingresamos venta
    @PostMapping("/")
    public ResponseEntity<VentaSimpleDto> saveVenta(@RequestBody VentaDto objNuevo){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaService.saveVenta(objNuevo));
    }
    
    //Eliminamos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable Long id){
        ventaService.deleteVenta(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    //Actualizamos 
    @PutMapping("/{id}")
    public ResponseEntity<VentaSimpleDto> updateVenta(@PathVariable Long id, @RequestBody VentaDto objActualizado){
        return ResponseEntity.ok(ventaService.updateVenta(id, objActualizado));
    }
    
    //Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<VentaSimpleDto> patchVenta(@PathVariable Long id, @RequestBody VentaDto objDto){
        return ResponseEntity.ok(ventaService.patchVenta(id, objDto));
    }
    
    //Agregar productos a Venta existente
    @PostMapping("/agregar-productos/{id}")
    public ResponseEntity<VentaSimpleDto> addProductosAventa(@PathVariable Long id, @RequestBody List<VentaProductoDto> productosNuevos) {
        return ResponseEntity.ok(ventaService.addProductosAVenta(id, productosNuevos));
    }
    
    //Eliminar productos de Venta existente
    @DeleteMapping("/eliminar-productos/{id}")
    ResponseEntity<?> eliminarProductosDeVenta(@PathVariable Long id, @RequestBody List<VentaProductoDto> productosEliminados){
        return ResponseEntity.ok(ventaService.deleteProductosDeVenta(id, productosEliminados));
    }
}
