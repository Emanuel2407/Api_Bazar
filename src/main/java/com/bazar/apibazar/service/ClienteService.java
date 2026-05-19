package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.cliente.ClientePatchDto;
import com.bazar.apibazar.dto.cliente.ClienteRequestDto;
import com.bazar.apibazar.dto.cliente.ClienteResponseDto;
import com.bazar.apibazar.exception.ClienteNotFoundException;
import com.bazar.apibazar.exception.UnauthorizedOperationException;
import com.bazar.apibazar.model.Cliente;
import com.bazar.apibazar.model.UserSec;
import com.bazar.apibazar.repository.IClienteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bazar.apibazar.security.jwt.CustomUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    //Método propio para extraer del objeto Authentication guardado en el SecurityContext el id del cliente relacionado con el usuario autenticado
    @Override
    public Long getAuthenticatedClientId(){

        //Sacamos objeto Authentication creado y guardado en el Security Context con base a la información almacenada en el token de autenticación
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Sacamos objeto Object con el Principal
        Object principalObj = authentication.getPrincipal();

        //Válidamos que el Principal sea instancia de CustomUserPrincipal
        if(!(principalObj instanceof CustomUserPrincipal principal)){
            throw new UnauthorizedOperationException("Usuario no autorizado");
        }

        //Del Principal obtenemos el ID del cliente que está haciendo la compra
        Long clienteId = principal.getClientId();

        //Si el usuario no es cliente quiere decir que clienteId=null, por lo que informamos el error
        if(clienteId == null){throw new UnauthorizedOperationException("El usuario no es cliente, por lo que no puede realizar la operación");}

        return clienteId;
    }

    @Override
    public void validarDisponibilidadCliente(Cliente objCliente){
        if(!objCliente.isActive()){throw new ClienteNotFoundException("El cliente con id: " + objCliente.getIdCliente() + " está deshabilitado");}
    }

    /*Método de construir el DTO para exponer a un cliente*/
    @Override
    public ClienteResponseDto sacarClienteSimple(Cliente objCliente){

        return new ClienteResponseDto(objCliente.getIdCliente(), objCliente.getNombre(), objCliente.getApellido(),
                objCliente.getDocumento(), objCliente.isActive());

    }

    //Método propio para consultar los datos de un cliente y en caso de que no exista, excepción personalizada
    @Override
    public Cliente findCliente(Long id) {
        Optional<Cliente> objCliente = clienteRepository.findById(id);

        if(objCliente.isEmpty()){throw new ClienteNotFoundException("No se encontró cliente con id: " + id);}

        return objCliente.get();

    }

    @Transactional(readOnly = true)
    @Override
    public List<ClienteResponseDto> getClientesSimples() {
        
        //Lista que va a contener a todos los clientes en su formato de respuesta
        List<ClienteResponseDto> listClientes = new ArrayList<>();
        
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
    public ClienteResponseDto findClienteSimple(Long id) {

        //Buscamos cliente y confirmamos existencia
        Cliente objCliente = findCliente(id);
        
        //Llamamos la método que se va a encargar de transformar un Cliente ordinario en un DTO de respuesta.
        return(sacarClienteSimple(objCliente));
        
        
    }

    @Transactional
    @Override
    public ClienteResponseDto saveCliente(ClienteRequestDto objNuevo) {
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
    public ClienteResponseDto updateCliente(Long id, ClienteRequestDto objActualizado) {
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
    public ClienteResponseDto patchCliente(Long id, ClientePatchDto objDto) {
        Cliente objCliente = findCliente(id);

        //Validamos que el cliente esté disponible
        validarDisponibilidadCliente(objCliente);

        //Actualizamos solo los datos enviados
        if (objDto.nombre() != null) {
            //Si se manda a actualizar el nombre, válidamos que no sea una cadena vacía
            if(objDto.nombre().isBlank()){throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre no puede estar vacío"
            );}
            objCliente.setNombre(objDto.nombre());
        }

        if (objDto.apellido() != null) {
            //Validamos que no sea una cadena vacía
            if(objDto.apellido().isBlank()){throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El apellido no puede estar vacío"
            );}
            objCliente.setApellido(objDto.apellido());
        }

        if (objDto.documento() != null) {
            //Se válida que el String no esté vacío o lleno de espacios
            if(objDto.documento().isBlank()){throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El documento no puede estar vacío"
            );}
            objCliente.setDocumento(objDto.documento());
        }

        //Devolvemos cliente
        return sacarClienteSimple(objCliente);
    }

    @Transactional
    @Override
    public ClienteResponseDto updateMe(ClienteRequestDto updatedCliente) {
        //Buscamos el ID del cliente que está autenticado y delegamos al método creado para actualización total
        return this.updateCliente(
                getAuthenticatedClientId(), updatedCliente
        );

    }

    @Transactional
    @Override
    public ClienteResponseDto patchMe(ClientePatchDto updatedCliente) {
        //Buscamos el ID del cliente que está autenticado y delegamos la actualización al método creado para actualización parcial
        return this.patchCliente(
                getAuthenticatedClientId(), updatedCliente
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ClienteResponseDto findMe() {
        //Buscamos cliente por su id guardado en el SecurityContext
        return sacarClienteSimple(findCliente(
                getAuthenticatedClientId())
        );

    }

}
