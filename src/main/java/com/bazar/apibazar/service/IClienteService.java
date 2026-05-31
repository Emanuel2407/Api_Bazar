package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.cliente.ClientePatchDto;
import com.bazar.apibazar.dto.cliente.ClienteRequestDto;
import com.bazar.apibazar.dto.cliente.ClienteResponseDto;
import com.bazar.apibazar.model.Cliente;

import java.util.List;

public interface IClienteService {

   /**
    * Consulta todos los clientes registrados.
    */
    List<ClienteResponseDto> getClientesSimples();

    ClienteResponseDto findClienteSimple(Long id);

    Cliente findCliente(Long id);

    ClienteResponseDto sacarClienteSimple(Cliente objCliente);

    void validarDisponibilidadCliente(Cliente objCliente);
    
    void suspendCliente(Long id);
    
    ClienteResponseDto updateCliente(Long id, ClienteRequestDto objActualizado);
    
    ClienteResponseDto patchCliente(Long id, ClientePatchDto updatedCliente);

    /**
     * Implementación de ownership para actualizar
     * solo los datos del cliente autenticado.
     * */
    ClienteResponseDto updateMe(ClienteRequestDto updatedCliente);

    /**
     * Implementación de ownership para actualizar
     * parcialmente los datos del cliente autenticado.
     * */
    ClienteResponseDto patchMe(ClientePatchDto updatedCliente);

    /**
     * Obtiene la identidad del cliente autenticado.
     */
    Long getAuthenticatedClientId();

    /**
     * Consulta los datos del cliente autenticado
     */
    ClienteResponseDto findMe();

}
