package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.cliente.ClientePatchDto;
import com.bazar.apibazar.dto.cliente.ClienteRequestDto;
import com.bazar.apibazar.dto.cliente.ClienteResponseDto;
import com.bazar.apibazar.exception.ClienteNotFoundException;
import com.bazar.apibazar.exception.UnauthorizedOperationException;
import com.bazar.apibazar.model.Cliente;
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

    private final IClienteRepository clienteRepository;
    private final IUserService userService;

    public ClienteService(IClienteRepository clienteRepository, IUserService userService) {
        this.clienteRepository = clienteRepository;
        this.userService = userService;
    }

    /**
     * Obtiene cliente autenticado en el Security Context y
     * retorna su id.
     * */
    @Override
    public Long getAuthenticatedClientId(){

       //Recupera la autenticación actual del usuario
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Extrae objeto Principal con la identidad del usuario.
        Object principalObj = authentication.getPrincipal();

        //Valida que sea instancia de nuestro Principal personalizado
        if(!(principalObj instanceof CustomUserPrincipal principal)){
            throw new UnauthorizedOperationException("Usuario no autorizado");
        }

        Long clienteId = principal.getClientId();
        //Validamos que el usuario realmente sea cliente
        if(clienteId == null){throw new UnauthorizedOperationException("El usuario no es cliente, por lo que no puede realizar la operación");}

        return clienteId;
    }

    @Override
    public void validarDisponibilidadCliente(Cliente objCliente){
        if(!objCliente.isActive()){throw new ClienteNotFoundException("El cliente con id: " + objCliente.getIdCliente() + " está deshabilitado");}
    }

    /**
     * Construye DTO para exponer un cliente.
     * */
    @Override
    public ClienteResponseDto sacarClienteSimple(Cliente objCliente){

        return new ClienteResponseDto(objCliente.getIdCliente(), objCliente.getNombre(), objCliente.getApellido(),
                objCliente.getDocumento(), objCliente.isActive());

    }

   /**
    * Consulta un cliente por su id o lanza excepción
    * personalizada si no existe.
    * */
    @Override
    public Cliente findCliente(Long id) {
        Optional<Cliente> objCliente = clienteRepository.findById(id);

        if(objCliente.isEmpty()){throw new ClienteNotFoundException("No se encontró cliente con id: " + id);}

        return objCliente.get();

    }

    @Transactional(readOnly = true)
    @Override
    public List<ClienteResponseDto> getClientesSimples() {

        List<ClienteResponseDto> listClientes = new ArrayList<>();

        for(Cliente objCliente: clienteRepository.findAll()){
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
    public void suspendCliente(Long id) {

        Cliente objCliente = findCliente(id);

        //Se aplica un borrado lógico, desactivando al cliente para evitar perder datos de negocio importantes
        objCliente.setActive(false);

        /*Se suspende al cliente, pero el usuario puede seguir iniciando sesión solo que esta
            vez, solo podrá realizar operaciones de consulta y no ciertas operaciones
            de negocio como comprar, crear un carrito, agregar productos a carrito, etc.*/
    }

    @Transactional
    @Override
    public ClienteResponseDto updateCliente(Long id, ClienteRequestDto objActualizado) {
        Cliente objCliente = findCliente(id);

        validarDisponibilidadCliente(objCliente);

        objCliente.setNombre(objActualizado.nombre());
        objCliente.setApellido(objActualizado.apellido());
        objCliente.setDocumento(objActualizado.documento());

        return sacarClienteSimple(objCliente);
        
        
    }

    @Transactional
    @Override
    public ClienteResponseDto patchCliente(Long id, ClientePatchDto objDto) {
        Cliente objCliente = findCliente(id);

        validarDisponibilidadCliente(objCliente);

        if (objDto.nombre() != null) {
            //Si se manda a actualizar el nombre, válidamos que no sea una cadena vacía
            if(objDto.nombre().isBlank()){throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre no puede estar vacío"
            );}
            objCliente.setNombre(objDto.nombre());
        }

        if (objDto.apellido() != null) {
            if(objDto.apellido().isBlank()){throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El apellido no puede estar vacío"
            );}
            objCliente.setApellido(objDto.apellido());
        }

        if (objDto.documento() != null) {
            if(objDto.documento().isBlank()){throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El documento no puede estar vacío"
            );}
            objCliente.setDocumento(objDto.documento());
        }

        return sacarClienteSimple(objCliente);
    }

    @Transactional
    @Override
    public ClienteResponseDto updateMe(ClienteRequestDto updatedCliente) {
        /*Buscamos el ID del cliente autenticado y
          delegamos actualización a updateCliente()l */
        return this.updateCliente(
                getAuthenticatedClientId(), updatedCliente
        );

    }

    @Transactional
    @Override
    public ClienteResponseDto patchMe(ClientePatchDto updatedCliente) {
        /*Buscamos el ID del cliente autenticado y delegamos la
          actualización a patchCliente()*/
        return this.patchCliente(
                getAuthenticatedClientId(), updatedCliente
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ClienteResponseDto findMe() {
        return sacarClienteSimple(findCliente(
                getAuthenticatedClientId())
        );

    }

}
