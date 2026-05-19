package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.cliente.ClienteDto;
import com.bazar.apibazar.dto.cliente.ClienteSimpleDto;
import com.bazar.apibazar.service.IClienteService;
import java.util.List;
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
    private final IClienteService clienteService;

    //Inyección de dependencia por constructor
    public ClienteController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

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

    //Traer los datos del cliente autenticado
    @GetMapping("/me")
    public ResponseEntity<ClienteSimpleDto> findMe(){
        return ResponseEntity.ok(
                clienteService.findMe()
        );
    }

    //Ingresamos 
    @PostMapping("/")
    public ResponseEntity<ClienteSimpleDto> saveCliente(@RequestBody ClienteDto objNuevo){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteService.saveCliente(objNuevo));
    }
    
    //Eliminamos usando Soft Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> disableCliente(@PathVariable Long id){
        clienteService.disableCliente(id);
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
    
}
