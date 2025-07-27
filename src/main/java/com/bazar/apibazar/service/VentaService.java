package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.VentaDto;
import com.bazar.apibazar.dto.VentaResumenDto;
import com.bazar.apibazar.model.Cliente;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.model.Venta;
import com.bazar.apibazar.repository.IClienteRepository;
import com.bazar.apibazar.repository.IProductoRepository;
import com.bazar.apibazar.repository.IVentaRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaService implements IVentaService{
            
    //Inyección de dependecia para VentaRepository
    @Autowired
    IVentaRepository ventaRepository;
    
    //Inyección de dependencia para ProductoService
    @Autowired
    IProductoService productoService;
    
    //Inyección de dependecia para ClienteService
    @Autowired
    IClienteService clienteService;
    
    //Método propiio para cálcular el total de una determinada venta
    private Double calcularTotal(List<Producto> listProducto){
        Double total = 0.0;  
        
        for(Producto obj: listProducto){
            
            Producto objProducto = productoService.findProducto(obj.getIdProducto());
            
            if(objProducto != null){
                total += objProducto.getCosto();
            }
        }
        
        return total;
    } 
    
    //Método propio para reponer una cantidad a los productos de una determinada venta
    private void adicionarCantidadProducto(List<Producto> listProductos){
        
        for(Producto obj: listProductos){
            Producto objProducto = productoService.findProducto(obj.getIdProducto());   
            
            if(objProducto != null){
                objProducto.setCantidadDisponible(objProducto.getCantidadDisponible() + 1);
            }
           
        }
    }
    
    //Método propio para pasar de una lista que solo tiene los ids de los productos, a una que tiene los productos completos
    private List<Producto> buscarProductosDeVenta(List<Producto> listIdsProductos){
        
        //Creamos lista que va a contener solo los productos que esten disponibles 
        List<Producto> productosDisponibles = new ArrayList<>();
        
        //Recorremos lista de productos de la venta
        for(Producto obj: listIdsProductos){
            
            //Traemos al producto asociado con el id que vino en objNuevo
            Producto objProducto = productoService.findProducto(obj.getIdProducto());
            
            //Si el producto existe y tiene diponibilidad  lo agregamos a la lista y descontamos, sino simplemente no se agg
            if(objProducto != null && objProducto.getCantidadDisponible() > 0 ){
                
                productosDisponibles.add(objProducto);
                
                //Le restamos una cantidad al producto ya que esta afue asignada a una venta
                objProducto.setCantidadDisponible(objProducto.getCantidadDisponible() - 1);
            }   
        }
        
        //Mandamos la lista completa de regreso
        return productosDisponibles;
    }
    
    
    @Override
    public List<Venta> getVentas() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta findVenta(Long id) {
        Optional<Venta> objVenta = ventaRepository.findById(id);
        
        if(objVenta.isEmpty()){return null;}
        
        return objVenta.get();
    }

    @Override
    public void saveVenta(VentaDto objNuevo) {
        Venta objVenta = new Venta();
        
        //Migramos datos del objeto Dto al objeto Venta
        objVenta.setFechaVenta(objNuevo.getFechaVenta());   
        objVenta.setCliente(objNuevo.getCliente());
        
        //Agregamos lista de productos disponibles a la venta
        List<Producto> listProductos = buscarProductosDeVenta(objNuevo.getListProductos());
        objVenta.setListProductos(listProductos);
        
        //Ahora calculamos el total con base a la lista de productos que sabemos que tienen disponibilidad
        objVenta.setTotal(calcularTotal(listProductos));
        ventaRepository.save(objVenta);
        
    }

    @Override
    public boolean deleteVenta(Long id) {
        
        if(ventaRepository.existsById(id)){
            
            //Al momento de eliminar una venta, se deben sumar los producto que estaban en ella, ya que ahora están disponibles
            adicionarCantidadProducto(findVenta(id).getListProductos());
            
            //Ahora si eliminamos la venta 
            ventaRepository.deleteById(id);
            
            return true;
        }
        
        return false;
    }

    @Override
    public Venta updateVenta(Long id, VentaDto objActualizado) {
        Venta objVenta = findVenta(id);
        
        //Si no existe retornamos null
        if(objVenta == null){return objVenta;}
        
        //Actualizamos datos de la venta en cuestion
        objVenta.setFechaVenta(objActualizado.getFechaVenta());
        
        //Buscamos y agregamos el respectivo cliente a la venta
        if(objActualizado.getCliente() == null){  
            
            //Si no viene un Cliente en los datos de la venta nueva, se registra como null
            objVenta.setCliente(null);
            
        }else{
            //Si sí veiene lo buscamos y agregamos
            Cliente objCliente = clienteService.findCliente(objActualizado.getCliente().getIdCliente());

            objVenta.setCliente(objCliente);
        } 
        
        //Debemos adicionar una cantidad a los productos de la venta que serán reemplazados, es decir los que están en objVenta
        adicionarCantidadProducto(objVenta.getListProductos());          
        
        //Agregamos la lista los productos nuevos a la venta 
        List<Producto> listProductos = buscarProductosDeVenta(objActualizado.getListProductos());
        objVenta.setListProductos(listProductos);
        
        //Calculamos el total con base a los productos que sabemos que sí estan disponibles
        objVenta.setTotal(calcularTotal(listProductos));
        
        //Actualizamos la venta
        ventaRepository.save(objVenta);
        
        return objVenta;
    }

    @Override
    public Venta patchVenta(Long id, VentaDto objDto) {
        Venta objVenta = findVenta(id);
        
        //Si no existe retornamos null
        if(objVenta == null){return objVenta;}
        
        
        //Actualizamos solo los datos que especifique el usuario
        if(objDto.getFechaVenta() != null){objVenta.setFechaVenta(objDto.getFechaVenta());}
        
        if(objDto.getCliente() != null){
 
            //Buscamos el cliente para agregarlo
            Cliente objCliente = clienteService.findCliente(objDto.getCliente().getIdCliente());
            
            //Lo agregamos
            objVenta.setCliente(objCliente);
        
        }
        
        if(!objDto.getListProductos().isEmpty()){     
            
            ////Agregamos primero los nuevos productos que vienen en objDto
            List<Producto> listProductos = buscarProductosDeVenta(objDto.getListProductos());
            
            //Agregamos ahora los que ya estaban registrado en la venta si es que habia    
            for(Producto obj: objVenta.getListProductos()){
                listProductos.add(obj);
             }
     
            //Agregamos la lista a la respectiva venta
            objVenta.setListProductos(listProductos);
            
            
            //Actualizamos el total 
            objVenta.setTotal(calcularTotal(listProductos));
       
        }
        
        ventaRepository.save(objVenta);
        
        return objVenta;
    }

    @Override
    public List<Producto> productosDeVenta(Long id) {
        Venta objVenta = findVenta(id);
        
        //Si no se encuentra la venta, se retorna una lista vacía
        if(objVenta == null){return new ArrayList<>();}
        
        return objVenta.getListProductos();
    }

    @Override
    public String ventasDelDia(LocalDate fechaVenta) {
        double total = 0;
        int contVentas= 0;
        boolean existe = false;
        
        for(Venta objVenta: getVentas()){
            
            //Comparamos la fecha de cada Venta a ver si coincide con la que viene en parametro
            if(objVenta.getFechaVenta().equals(fechaVenta)){
                total += objVenta.getTotal();
                contVentas++;
                
                //Cuando entre al menos una vez, confirmaremos que la venta existe
                if(existe == false){existe = true;}
            }
        }
        
        //Si confirmamos que la venta existe, devuelve el monto total y cantidad de ventas
        if(existe){
            return "El total de las ventas del día " + fechaVenta + " es: $" + total + ".\n"
                    + "Y se ha hecho un total de " + contVentas + " venta(s).";
        }
        
        //Si no existe la venta se retorna null
        return null;
        
    }

    @Override
    public VentaResumenDto findMayorVenta() {
        
        //Lista de todas las ventas
        List<Venta> listVentas = getVentas();
        
        //Si no hay ventas registradas, retornamos null
        if(listVentas.isEmpty()){return null;}
        
        //Le damos valor de la primera venta de la lista para empezar a comparar
        Venta mayorVenta = listVentas.get(0);      
        
        //Buscamos la venta con mayor monto que va a terminar en "mayorVenta"
        for(Venta objVenta: listVentas){
            
            if(objVenta.getTotal() > mayorVenta.getTotal()){
                mayorVenta = objVenta;
            }
        }
        
        //Ahora migramos atributos a "VentaResumenDto" y retornamos
        VentaResumenDto mayorVentaDto = new VentaResumenDto();
        
        mayorVentaDto.setIdVenta(mayorVenta.getIdVenta());
        mayorVentaDto.setTotal(mayorVenta.getTotal());
        mayorVentaDto.setCantProductos(mayorVenta.getListProductos().size());
        if(mayorVenta.getCliente() != null){
            mayorVentaDto.setNombreCliente(mayorVenta.getCliente().getNombre());
            mayorVentaDto.setApellidoCliente(mayorVenta.getCliente().getApellido());
        }
        
       
        return mayorVentaDto;  
        
    }
    
}
