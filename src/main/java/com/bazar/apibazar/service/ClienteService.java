package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.ClienteDto;
import com.bazar.apibazar.model.Cliente;
import com.bazar.apibazar.repository.IClienteRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService implements IClienteService{
    
    //Inyección de dependencia para ClienteRepository
    @Autowired
    IClienteRepository clienteRepository;
    
    @Override
    public List<Cliente> getClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente findCliente(Long id) {
        Optional<Cliente> objCliente = clienteRepository.findById(id);
        
        if(objCliente.isEmpty()){
            return null;
        }
        
        return objCliente.get();
        
    }

    @Override
    public void saveCliente(ClienteDto objNuevo) {
        Cliente objCliente = new Cliente();
        
        objCliente.setNombre(objNuevo.getNombre());
        objCliente.setApellido(objNuevo.getApellido());
        objCliente.setDocumento(objNuevo.getDocumento());

        clienteRepository.save(objCliente);
       
    }

    @Override
    public boolean deleteCliente(Long id) {
        
        if(clienteRepository.existsById(id)){
            clienteRepository.deleteById(id);
            
            return true;
            
        }
        
        return false;
    }

    @Override
    public Cliente updateCliente(Long id, ClienteDto objActualizado) {
        Cliente objCliente = findCliente(id);
        
        if(objCliente == null){return objCliente;}
        
        objCliente.setNombre(objActualizado.getNombre());
        objCliente.setApellido(objActualizado.getApellido());
        objCliente.setDocumento(objActualizado.getDocumento());
        
        clienteRepository.save(objCliente);
        
        return objCliente;
        
        
    }

    @Override
    public Cliente patchCliente(Long id, ClienteDto objDto) {
        Cliente objCliente = findCliente(id);
        
        if(objCliente == null){return objCliente;}
        
        if(objDto.getNombre() != null){objCliente.setNombre(objDto.getNombre());}
        if(objDto.getApellido() != null){objCliente.setApellido(objDto.getApellido());}
        if(objDto.getDocumento() != null){objCliente.setDocumento(objDto.getDocumento());}
        
        clienteRepository.save(objCliente);
        
        return objCliente;
    }
    
}
