package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.GetVentaDto;
import com.bazar.apibazar.dto.VentaDto;
import com.bazar.apibazar.dto.VentaProductoDto;
import com.bazar.apibazar.dto.VentaResumenDto;
import com.bazar.apibazar.model.Venta;
import com.bazar.apibazar.service.IVentaService;
import com.bazar.apibazar.utils.ResponseUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    @ResponseBody
    public List<GetVentaDto> getVentas(){
        return ventaService.getVentasSimples();
    }
    
    //Traer uno
    @GetMapping("/{id}")
    public ResponseEntity<?> findVenta(@PathVariable Long id){
        GetVentaDto objVenta = ventaService.findVentaSimple(id);
        
        if(objVenta == null){
            //Si no existe registro, se le envia un error personalizado al usuario indicandoselo
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.notFound(id));
        }
        
        return ResponseEntity.ok(objVenta);
        
    }
    
    //Traer productos de una venta
    @GetMapping("/productos/{id}")
    public ResponseEntity<?> productosDeVenta(@PathVariable Long id){
        GetVentaDto objVenta = ventaService.findVentaSimple(id);
        
        if(objVenta == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.notFound(id));
        }
        
        return ResponseEntity.ok(ventaService.productosDeVenta(id));
    }
    
    //Traer el monto total y la cantidad de ventas de un determinado día
    @GetMapping("/fecha/{fechaVenta}")
    public ResponseEntity<?> ventasDelDia(@PathVariable LocalDate fechaVenta){
        String infoVentas = ventaService.ventasDelDia(fechaVenta);
         
        //Traemos la respuesta notFound y le cambiamos el mensaje para que sea coherente con el método
        Map<String, Object> notFound = ResponseUtil.notFound(-1L);
        notFound.put("mensaje", "No se encontró venta con fecha: " + fechaVenta);
        
        if(infoVentas == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
        }
        
        return ResponseEntity.ok(infoVentas);
    }
    
    @GetMapping("/mayor_venta")
    public ResponseEntity<?> findMayorVenta(){
        VentaResumenDto ventaMayorDto = ventaService.findMayorVenta();
        
        //Traemos la respuesta notFound y le cambiamos el mensaje para que sea coherente con el método
        Map<String, Object> notFound = ResponseUtil.notFound(-1L);
        notFound.put("mensaje", "No hay ventas registradas");
                
        if(ventaMayorDto == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
        }
        
        return ResponseEntity.ok(ventaMayorDto);
        
    }
    
    //Ingresamos 
    @PostMapping("/")
    public void saveVenta(@RequestBody VentaDto objNuevo){ventaService.saveVenta(objNuevo);}
    
    //Eliminamos
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVenta(@PathVariable Long id){
        
        if(ventaService.deleteVenta(id)){
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.notFound(id));
    }
    
    //Actualizamos 
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVenta(@PathVariable Long id, @RequestBody VentaDto objActualizado){
        Venta objVenta = ventaService.updateVenta(id, objActualizado);
        
        if(objVenta == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.notFound(id));
        }
        
        return ResponseEntity.ok(objVenta);
    }
    
    //Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchVenta(@PathVariable Long id, @RequestBody VentaDto objDto){
        Venta objVenta = ventaService.patchVenta(id, objDto);
        
        if(objVenta == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.notFound(id));
        }
        
        return ResponseEntity.ok(objVenta);
    }
    
    
    //Agregar productos a Venta existente
    @PatchMapping("/agregarProductos/{id}")
    public ResponseEntity<?> addProductosAventa(@PathVariable Long id, @RequestBody List<VentaProductoDto> productosNuevos){
        Venta objVenta = ventaService.addProductosAVenta(id, productosNuevos);
        
        if(objVenta == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.notFound(id));
        }
        
        return ResponseEntity.ok(objVenta);
    }
    
}
