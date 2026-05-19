package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.cliente.ClienteRequestDto;
import com.bazar.apibazar.dto.cliente.ClienteResponseDto;
import com.bazar.apibazar.model.Cliente;

import java.util.List;

public interface IClienteService {
    
    /*Como queremos que este método nos devuelva sólo la lista de productos que estan relacionados con la 
    respectiva Venta de un  cliente y no la lista completa de la tabla intermedia VentaProducto. Entonces para
    lograsr esto hacemos la implementacion de una clase Dto que devuelva lo mismo que la clase Cliente normal 
    excepto que la lista de VentaProducto de las ventas del cliente se va a cambiar por una lista de 
    productos normalita*/
    List<ClienteResponseDto> getClientesSimples();
    
    //Se devuelve objeto Dto con un Cliente que tiene ventas con simples productos
    ClienteResponseDto findClienteSimple(Long id);

    //Consultar cliente por ID y retornar directamente objeto de entidad
    Cliente findCliente(Long id);

    //Método para construir un DTO de exposición de un cliente a partir de los datos que este tenga guardado en la base de datos
    ClienteResponseDto sacarClienteSimple(Cliente objCliente);

    //Validar que un cliente no esté deshabilitado
    void validarDisponibilidadCliente(Cliente objCliente);

    ClienteResponseDto saveCliente(ClienteRequestDto objNuevo);
    
    void disableCliente(Long id);
    
    ClienteResponseDto updateCliente(Long id, ClienteRequestDto objActualizado);
    
    ClienteResponseDto patchCliente(Long id, ClienteRequestDto objDto);

        //Método para actualizar completamente los datos del cliente que está autenticado
    ClienteResponseDto updateMe(ClienteRequestDto updatedCliente);

    //Método para actualizar parcialmente los datos del cliente que está autenticado
    ClienteResponseDto patchMe(ClienteRequestDto updatedCliente);

    Long getAuthenticatedClientId();

    ClienteResponseDto findMe();

}
