package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.cliente.ClienteDto;
import com.bazar.apibazar.dto.cliente.ClienteSimpleDto;
import com.bazar.apibazar.exception.ClienteNotFoundException;
import com.bazar.apibazar.model.Cliente;
import com.bazar.apibazar.model.UserSec;
import com.bazar.apibazar.repository.IClienteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bazar.apibazar.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService implements IClienteService{
    
    //Inyección de dependencia para ClienteRepository
    private final IClienteRepository clienteRepository;
    //Inyección de dependencia para service de componente "user"
    private final IUserService userService;
    //Inyección de dependencia por constructor
    public ClienteService(IClienteRepository clienteRepository, IUserService userService) {
        this.clienteRepository = clienteRepository;
        this.userService = userService;
    }

    private void validarDisponibilidadCliente(Cliente objCliente){
        if(!objCliente.isActive()){throw new ClienteNotFoundException("El cliente con id: " + objCliente.getIdCliente() + " está deshabilitado");}
    }

    /*Método de construir el DTO para exponer a un cliente*/
    @Override
    public ClienteSimpleDto sacarClienteSimple(Cliente objCliente){

        return new ClienteSimpleDto(objCliente.getIdCliente(), objCliente.getNombre(), objCliente.getApellido(),
                objCliente.getDocumento());

    }

    //Método propio para consultar los datos de un cliente y en caso de que no exista, excepción personalizada
    private Cliente findCliente(Long id) {
        Optional<Cliente> objCliente = clienteRepository.findById(id);

        if(objCliente.isEmpty()){throw new ClienteNotFoundException("No se encontró cliente con id: " + id);}

        return objCliente.get();

    }

    @Transactional(readOnly = true)
    @Override
    public List<ClienteSimpleDto> getClientesSimples() {
        
        //Lista que va a contener a todos los clientes en su formato de respuesta
        List<ClienteSimpleDto> listClientes = new ArrayList<>();
        
        //Recorrer los clientes registrados
        for(Cliente objCliente: clienteRepository.findAll()){
            
            /*Llamamos la método que se va a encargar de transformar los datos de un Cliente de la base de datos a un DTO de respuesta.
            Le mandamos como parámetro cada uno de los clientes registrados y vamos agregando a la lista*/
            listClientes.add(
                    sacarClienteSimple(objCliente)
            );
        }
        
        return listClientes;
    }

    @Transactional(readOnly = true)
    @Override
    public ClienteSimpleDto findClienteSimple(Long id) {

        //Buscamos cliente y confirmamos existencia
        Cliente objCliente = findCliente(id);
        
        //Llamamos la método que se va a encargar de transformar un Cliente ordinario en un DTO de respuesta.
        return(sacarClienteSimple(objCliente));
        
        
    }

    @Transactional
    @Override
    public ClienteSimpleDto saveCliente(ClienteDto objNuevo) {
        Cliente objCliente = new Cliente();

        //Agregamos datos del usuario al objeto que se va a persistir
        objCliente.setNombre(objNuevo.nombre());
        objCliente.setApellido(objNuevo.apellido());
        objCliente.setDocumento(objNuevo.documento());
        
        //guardamos al cliente en la db
        clienteRepository.save(objCliente);

        return sacarClienteSimple(objCliente);
    }

    @Transactional
    @Override
    public void disableCliente(Long id) {
        //Se busca el cliente y se confirma existencia
        Cliente objCliente = findCliente(id);

        //Se aplica un borrado lógico, desactivando al cliente para evitar perder datos de negocio importantes
        objCliente.setActive(false);
        //Buscamos usuario con el que sé auténtica el cliente para deshabilitarlo también
        UserSec user = userService.findByClient(id);
        user.setEnabled(false);

    }

    @Transactional
    @Override
    public ClienteSimpleDto updateCliente(Long id, ClienteDto objActualizado) {
        Cliente objCliente = findCliente(id);

        //Validamos que el cliente esté disponible
        validarDisponibilidadCliente(objCliente);

        //Actualizamos datos del cliente
        objCliente.setNombre(objActualizado.nombre());
        objCliente.setApellido(objActualizado.apellido());
        objCliente.setDocumento(objActualizado.documento());

        //Con transactional se detecta el cambio en el contexto de seguridad y Hibernate actualiza en la bd sin necesidad de un save()

        //Devolvemos cliente
        return sacarClienteSimple(objCliente);
        
        
    }

    @Transactional
    @Override
    public ClienteSimpleDto patchCliente(Long id, ClienteDto objDto) {
        Cliente objCliente = findCliente(id);

        //Validamos que el cliente esté disponible
        validarDisponibilidadCliente(objCliente);

        //Actualizamos solo los datos enviados
        if (objDto.nombre() != null) {
            objCliente.setNombre(objDto.nombre());
        }
        if (objDto.apellido() != null) {
            objCliente.setApellido(objDto.apellido());
        }
        if (objDto.documento() != null) {
            objCliente.setDocumento(objDto.documento());
        }

        //Devolvemos cliente
        return sacarClienteSimple(objCliente);
    }

}
