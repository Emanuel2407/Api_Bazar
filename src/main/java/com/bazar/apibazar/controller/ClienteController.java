package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.ClienteDto;
import com.bazar.apibazar.model.Cliente;
import com.bazar.apibazar.service.IClienteService;
import com.bazar.apibazar.utils.ResponseUtil;
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
    
    //Inyección de dependecia para ClienteService
    @Autowired
    IClienteService clienteService;
    
    //Traer todos
    @GetMapping("/")
    @ResponseBody
    public List<Cliente> getClientes(){
        return clienteService.getClientes();
    }
    
    //Traer uno
    @GetMapping("/{id}")
    public ResponseEntity<?> findCliente(@PathVariable Long id){
        Cliente objCliente = clienteService.findCliente(id);
        
        if(objCliente == null){
            //Si no existe registro, se le envia un error personalizado al usuario indicandoselo
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.notFound(id));
        }
        
        return ResponseEntity.ok(objCliente);
        
    }
    
    //Ingresamos 
    @PostMapping("/")
    public void saveCliente(@RequestBody ClienteDto objNuevo){clienteService.saveCliente(objNuevo);}
    
    //Eliminamos
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCliente(@PathVariable Long id){
        
        if(clienteService.deleteCliente(id)){
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.notFound(id));
    }
    
    //Actualizamos 
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCliente(@PathVariable Long id, @RequestBody ClienteDto objActualizado){
        Cliente objCliente = clienteService.updateCliente(id, objActualizado);
        
        if(objCliente == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.notFound(id));
        }
        
        return ResponseEntity.ok(objCliente);
    }
    
    //Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchCliente(@PathVariable Long id, @RequestBody ClienteDto objDto){
        Cliente objCliente = clienteService.patchCliente(id, objDto);
        
        if(objCliente == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.notFound(id));
        }
        
        return ResponseEntity.ok(objCliente);
    }
}
