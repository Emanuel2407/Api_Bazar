package com.bazar.apibazar.controller;

import com.bazar.apibazar.dto.cliente.ClientePatchDto;
import com.bazar.apibazar.dto.cliente.ClienteRequestDto;
import com.bazar.apibazar.dto.cliente.ClienteResponseDto;
import com.bazar.apibazar.service.IClienteService;
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
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<ClienteResponseDto>> getClientes(){
        return ResponseEntity.ok(clienteService.getClientesSimples());
    }
    
    //Traer uno
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> findCliente(@PathVariable Long id){
        return ResponseEntity.ok(clienteService.findClienteSimple(id));
        
    }

    //Traer los datos del cliente autenticado
    @GetMapping("/me")
    public ResponseEntity<ClienteResponseDto> findMe(){
        return ResponseEntity.ok(
                clienteService.findMe()
        );
    }
    
    //Método para suspender a un cliente y restringirle el acceso a las operaciones de negocio al usuario asociado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> suspendCliente(@PathVariable Long id){
        clienteService.suspendCliente(id);
        return ResponseEntity.noContent().build();
    }
    
    //Actualización total
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> updateCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequestDto objActualizado){
        return ResponseEntity.ok(clienteService.updateCliente(id, objActualizado));
    }
    
    //Actualización parcial
    @PatchMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> patchCliente(@PathVariable Long id, @Valid @RequestBody ClientePatchDto UpdatedCliente){
        return ResponseEntity.ok(clienteService.patchCliente(id, UpdatedCliente));
    }

    //Actualización total de cliente autenticado
    @PutMapping("/me")
    public ResponseEntity<ClienteResponseDto> updateMe(@Valid @RequestBody ClienteRequestDto objActualizado){
        return ResponseEntity.ok(clienteService.updateMe(objActualizado));
    }

    //Actualización parcial de cliente autenticado
    @PatchMapping("/me")
    public ResponseEntity<ClienteResponseDto> patchMe(@Valid @RequestBody ClientePatchDto updatedCliente){
        return ResponseEntity.ok(clienteService.patchMe(updatedCliente));
    }



}
