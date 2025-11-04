package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.ClienteDto;
import com.bazar.apibazar.dto.ClienteSimpleDto;
import com.bazar.apibazar.dto.ProductoDeVentaDto;
import com.bazar.apibazar.dto.VentaDeClienteDto;
import com.bazar.apibazar.model.Cliente;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.model.Venta;
import com.bazar.apibazar.model.VentaProducto;
import com.bazar.apibazar.repository.IClienteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService implements IClienteService{
    
    //Inyección de dependencia para ClienteRepository
    @Autowired
    IClienteRepository clienteRepository;
    
    @Autowired
    IVentaService ventaService;
    
    /*Método propio que se va a encargar de pasar de un cliente que tiene una serie de ventas y que estas
    ventas tienen objetos VentaProducto a un Cliente que tenga ventas con objetos Producto simples para evitar
    la recursividad infinita*/
    private ClienteSimpleDto sacarClienteSimple(Cliente objCliente){
        
        //Si no existe el cliente se retorna null
        if(objCliente == null){return null;}
        
        //Lista para almacenar todas las ventas en formato simple del cliente
        List<VentaDeClienteDto> listVentas = new ArrayList<>();
        
        //Recorrer ventas
        for(Venta objVenta: objCliente.getListVentas()){
            
            //Lista para almacenar todos los productos en formato simple de las ventas del cliente
            List<ProductoDeVentaDto> listProductos = new ArrayList<>();
            
            //Recorrer los productos que estan en formato VentaProducto para convertir a formato simple
            for(VentaProducto objVP: objVenta.getListProductos()){
                    
                //Sacar el producto asociado a la relación
                Producto objProducto = objVP.getProducto();
                    
                //Agregar cadad producto transformado a la lista que los almacena 
                listProductos.add(new ProductoDeVentaDto(objProducto.getIdProducto(), objProducto.getNombre(),
                        objProducto.getMarca(), objProducto.getCosto(), objVP.getCantidad(), objVP.getSubTotalVenta()));              
            }
               
            //Agregar cada venta simplificada con los productos simples a la lista de ventas 
            listVentas.add(new VentaDeClienteDto(objVenta.getIdVenta(), objVenta.getFechaVenta(), objVenta.getTotalVenta(),
                    objVenta.getCantidadTotalProductos(), listProductos));
                  
        }
        
        //Por último podemos crear el objeto Cliente simplificado y exponerlo
        return (new ClienteSimpleDto(objCliente.getIdCliente(), objCliente.getNombre(), objCliente.getApellido(), 
                objCliente.getDocumento(), listVentas));
        
    }
    
    @Override
    public List<ClienteSimpleDto> getClientesSimples() {
        
        //Lista que va a contener a todos los clientes en su formato simple
        List<ClienteSimpleDto> listClientes = new ArrayList<>();
        
        //Recorrer los clientes registrados
        for(Cliente objCliente: getClientes()){
            
            /*Llamamos la método que se va a encargar de transformar un Cliente ordinario en uno mas simple.
            Le mandamos como parametro cada uno de los clientes registrados y vamos agregando a la lista*/
            listClientes.add(sacarClienteSimple(objCliente));
        }
        
        return listClientes;
    }

    
    @Override
    public ClienteSimpleDto findClienteSimple(Long id) {
        
        Cliente objCliente = findCliente(id);
        
        if(objCliente == null){return null;}
        
        //Llamamos la método que se va a encargar de transformar un Cliente ordinario en uno mas simple.
        return(sacarClienteSimple(objCliente));
        
        
    }
    
    //Método privado get normalito
    private List<Cliente> getClientes() {
        return clienteRepository.findAll();
    }

    //Método privado find normalito
    private Cliente findCliente(Long id) {
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
        objCliente.setListVentas(objNuevo.getListVentas());
        
        clienteRepository.save(objCliente);
       
    }

    @Override
    public boolean deleteCliente(Long id) {
        
        Cliente objCliente = findCliente(id);
        
        if(objCliente != null){
            
            //Primero se elimina todas las venta que estaban asociadas con este cliente
            for(Venta objVenta: objCliente.getListVentas()){
                ventaService.deleteVenta(objVenta.getIdVenta());
            }
             
            //Luego eliminamos la venta
            clienteRepository.deleteById(id);
            
            return true;
            
        }
        
        return false;
    }

    @Override
    public ClienteSimpleDto updateCliente(Long id, ClienteDto objActualizado) {
        Cliente objCliente = findCliente(id);
        
        if(objCliente == null){return null;}
        
        objCliente.setNombre(objActualizado.getNombre());
        objCliente.setApellido(objActualizado.getApellido());
        objCliente.setDocumento(objActualizado.getDocumento());
        
        /*Para actualizar las ventas de un cliente, primero se deben elimiar las que estaban relacionadas 
        antes con el cliente ya que una venta no puede existir sin un cliente*/
        for(Venta objVenta: objCliente.getListVentas()){
            ventaService.deleteVenta(objVenta.getIdVenta());
        }
        
        //Una vez borradas, se le abre camino a las nuevas ventas
        objCliente.setListVentas(objActualizado.getListVentas());
        
        clienteRepository.save(objCliente);
        
        return sacarClienteSimple(objCliente);
        
        
    }

    @Override
    public ClienteSimpleDto patchCliente(Long id, ClienteDto objDto) {
        Cliente objCliente = findCliente(id);
        
        if(objCliente == null){return null;}
        
        if(objDto.getNombre() != null){objCliente.setNombre(objDto.getNombre());}
        if(objDto.getApellido() != null){objCliente.setApellido(objDto.getApellido());}
        if(objDto.getDocumento() != null){objCliente.setDocumento(objDto.getDocumento());}  
        
        /*La implementación del método path en las ventas del cliente se va a hacer de una forma particular,
        en vez de reemplazar todas las ventas anteriores por nuevas ventas, todas las ventas que lleguen en
        objDto se van a adicionar a las ventas que ya existian, esto para generar un poco más de lógica ya
        que no tiene mucho sentido cambiar las ventas compradas por un clienete por otras*/
        if(!objDto.getListVentas().isEmpty()){
            
            //Lista que contendrá tanto las ventas antiguas como las nuevas
            List<Venta> listVentas = new ArrayList<>();
            
            //Agregamos las antiguas
            for(Venta objVenta: objCliente.getListVentas()){listVentas.add(objVenta); }
            
            //Agregamos las nuevas
            for(Venta objVenta: objDto.getListVentas()){listVentas.add(objVenta);}
            
            //Las agregamos todas al cliente
            objCliente.setListVentas(objDto.getListVentas());
        }
        
        clienteRepository.save(objCliente);
        
        return sacarClienteSimple(objCliente);
    }

    
    
}
