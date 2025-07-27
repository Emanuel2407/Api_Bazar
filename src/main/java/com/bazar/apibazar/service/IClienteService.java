package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.ClienteDto;
import com.bazar.apibazar.model.Cliente;
import java.util.List;

public interface IClienteService {
    
    List<Cliente> getClientes();
    
    Cliente findCliente(Long id);
    
    void saveCliente(ClienteDto objNuevo);
    
    boolean deleteCliente(Long id);
    
    Cliente updateCliente(Long id, ClienteDto objActualizado);
    
    Cliente patchCliente(Long id, ClienteDto objDto);
}
