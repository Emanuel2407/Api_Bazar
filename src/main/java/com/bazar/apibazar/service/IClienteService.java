package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.ClienteAgregarVentasDto;
import com.bazar.apibazar.dto.ClienteDto;
import com.bazar.apibazar.dto.ClienteSimpleDto;
import com.bazar.apibazar.dto.VentaSimpleDto;
import com.bazar.apibazar.model.Cliente;
import java.util.List;

public interface IClienteService {
    
    /*Como queremos que este método nos devuelva sólo la lista de productos que estan relacionados con la 
    respectiva Venta de un  cliente y no la lista completa de la tabla intermedia VentaProducto. Entonces para
    lograsr esto hacemos la implementacion de una clase Dto que devuelva lo mismo que la clase Cliente normal 
    excepto que la lista de VentaProducto de las ventas del cliente se va a cambiar por una lista de 
    productos normalita*/
    List<ClienteSimpleDto> getClientesSimples();
    
    //Se devuelve objeto Dto con un Cliente que tiene ventas con simples productos
    ClienteSimpleDto findClienteSimple(Long id);
    
    void saveCliente(ClienteDto objNuevo);
    
    boolean deleteCliente(Long id);
    
    ClienteSimpleDto updateCliente(Long id, ClienteDto objActualizado);
    
    ClienteSimpleDto patchCliente(Long id, ClienteDto objDto);
    
    //Método para agregar una cierta cantidad de ventas a un cliente
    ClienteSimpleDto addVentasACliente(Long idCliente, ClienteAgregarVentasDto nuevasVentas);
}
