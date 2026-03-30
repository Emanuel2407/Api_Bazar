package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.ClienteVentasIdsDto;
import com.bazar.apibazar.dto.ClienteDto;
import com.bazar.apibazar.dto.ClienteSimpleDto;
import com.bazar.apibazar.service.IClienteService;
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
@RequestMapping("/clientes")
public class ClienteController {
    
    //Inyección de dependencia para ClienteService
    @Autowired
    IClienteService clienteService;
    
    //Traer todos
    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<List<ClienteSimpleDto>> getClientes(){
        return ResponseEntity.ok(clienteService.getClientesSimples());
    }
    
    //Traer uno
    @GetMapping("/{id}")
    public ResponseEntity<ClienteSimpleDto> findCliente(@PathVariable Long id){
        return ResponseEntity.ok(clienteService.findClienteSimple(id));
        
    }
    
    //Ingresamos 
    @PostMapping("/")
    public ResponseEntity<ClienteSimpleDto> saveCliente(@RequestBody ClienteDto objNuevo){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.saveCliente(objNuevo));
    }
    
    //Eliminamos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id){
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }
    
    //Actualización total
    @PutMapping("/{id}")
    public ResponseEntity<ClienteSimpleDto> updateCliente(@PathVariable Long id, @RequestBody ClienteDto objActualizado){
        return ResponseEntity.ok(clienteService.updateCliente(id, objActualizado));
    }
    
    //Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<ClienteSimpleDto> patchCliente(@PathVariable Long id, @RequestBody ClienteDto objDto){
        return ResponseEntity.ok(clienteService.patchCliente(id, objDto));
    }
    
    @PostMapping("/add-ventas/{id}")
    public ResponseEntity<ClienteSimpleDto> addVentasACliente(@PathVariable Long id, @RequestBody ClienteVentasIdsDto nuevasVentas){
        return ResponseEntity.ok(clienteService.addVentasACliente(id, nuevasVentas));
    }
    
    @DeleteMapping("/delete-ventas/{id}")
    public ResponseEntity<ClienteSimpleDto> DropVentasACliente(@PathVariable Long id, @RequestBody ClienteVentasIdsDto nuevasVentas){
        return ResponseEntity.ok(clienteService.dropVentasACliente(id, nuevasVentas));
        
    }
    
}
