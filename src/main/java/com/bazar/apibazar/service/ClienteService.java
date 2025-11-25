package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.ClienteAgregarVentasDto;
import com.bazar.apibazar.dto.ClienteDeVentaDto;
import com.bazar.apibazar.dto.ClienteDto;
import com.bazar.apibazar.dto.ClienteSimpleDto;
import com.bazar.apibazar.dto.ProductoDeVentaDto;
import com.bazar.apibazar.dto.VentaDeClienteDto;
import com.bazar.apibazar.dto.VentaDto;
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
    
    //Inyección de dependencia para ventaService
    @Autowired
    VentaService ventaService; 
    
    
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
    
    /*Método propio para cambiar las ventas Dto que vienen en un objeto ClienteDto a ventas nomales para 
    clientes normales*/
    private void addVentaDtoACliente(Cliente objCliente, List<VentaDto> listVentas){
        
        //Recorremos todas las VentaDto que vienen en la lista 
        for(VentaDto objVentaDto: listVentas){
            
            /*Cada ventaDto se guarda como una venta normal con el método saveVenta de VentaService y como
            este método devuelve la venta que se registró, la agregamos directamente a la lista de ventas 
            del cliente*/
            objCliente.getListVentas().add(ventaService.saveVenta(objVentaDto));
                
        }
        
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
            
        /*LLammos al método que se encargue de agregar al cliente todas las ventas que corresponden a los 
        objetos ventaDto que vienen en objNuevo */
        addVentaDtoACliente(objCliente, objNuevo.getListVentas());
        
        //Por último guardamos al cliente
        clienteRepository.save(objCliente);
       
    }

    @Override
    public boolean deleteCliente(Long id) {
        
        Cliente objCliente = findCliente(id);
        
        if(objCliente != null){
            
           /*Como no puede existir una venta sin su cliente, al momento de eliminar al cliente debemos eliminar
           todas las ventas que este tiene, y al momento de eliminar las ventas debemos eliminar también las 
           relaciones que cada una de estas tenga con los diferentes productos*/
            for(Venta objVenta: objCliente.getListVentas()){
               
                //El método deleteVenta se encarga tanto de borrar las relaciones como de borrar la venta 
                ventaService.deleteVenta(objVenta.getIdVenta());
            }
           
            //Limpiamos la lista del cliente para que quede vacia
            objCliente.getListVentas().clear();
           
            //Luego solo nos falta eliminar al cliente
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
        
        /*Se debe limpiar la lista del objeto Cliente en memoria para que no se tengan en cuenta las ventas
        ya eliminadas*/
        objCliente.getListVentas().clear();     
        
        /*Una vez borradas, se le abre camino a las nuevas ventas que viene en forma de objetos VentaDto las 
        cuales serán convertidas a ventas normales y agregadas a objCiente. Esto lo haremos por medio del 
        siguiente método:*/
        addVentaDtoACliente(objCliente, objActualizado.getListVentas());
        
        //Actualizamos cliente
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
        que no tiene mucho sentido cambiar las ventas compradas por un clienete por otras
        NOTA: Las nuevas ventas que se van a asignar deben crearse desde la misma request o petición ya que 
        por eso la lista de ventas nuevas son puros objetos Dtos. Por otro lado si se quiere hacer la asignación
        de una venta ya creada al Cliente solo con el id de de la venta, se debe usar el método ""*/
        if(!objDto.getListVentas().isEmpty()){
            
            /*Para esto solo bastará con llamar al método "addVentaDtoACliente" pero en este caso no vamos a 
            eliminar las ventas o relaciones que existian antes con el cliente*/
            addVentaDtoACliente(objCliente, objDto.getListVentas());
        }
        
        clienteRepository.save(objCliente);
        
        return sacarClienteSimple(objCliente);
    }
    
    
    public ClienteSimpleDto addVentasACliente(Long idCliente, ClienteAgregarVentasDto nuevasVentas){
        
        //Buscamos el cliente
        Cliente objCliente = findCliente(idCliente);
        
        if(objCliente == null){return null;}
        
        //Si la lista de los ids de las nuevas ventas está vacia, pues devolvemos al cliente con las ventas que ya tenía
        /*NOTA: La lista de IDs está dentro de la clase ClienteAgregarVentasDto, por lo que al método no va a
        llegar una lista directa con los IDs sino un objeto de esta clase que la contiene*/
        if(nuevasVentas.getVentasIds().isEmpty()){sacarClienteSimple(objCliente);}
        
        //Si no está vacía la recorremo
        for(Long idVenta: nuevasVentas.getVentasIds()){
            
            //Buscamos cada venta con el id correspondiente
            Venta objVenta = ventaService.findVenta(idVenta);
               
            //Vemos si la venta pertenece o no a un cliente
            ClienteDeVentaDto clienteDeVenta = ventaService.buscarClienteDeVenta(idVenta);
            
            //Verificamos si la Venta existe y si no pertence a ningún cliente
            if(objVenta == null || clienteDeVenta!=null){continue;}
            
            objCliente.getListVentas().add(objVenta);
            
            clienteRepository.save(objCliente);
            
        }
        
        return sacarClienteSimple(objCliente);
        
    }

    
    
}
