package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.GetVentaDto;
import com.bazar.apibazar.dto.ProductoDeVentaDto;
import com.bazar.apibazar.dto.VentaDto;
import com.bazar.apibazar.dto.VentaProductoDto;
import com.bazar.apibazar.dto.VentaResumenDto;
import com.bazar.apibazar.model.Cliente;
import com.bazar.apibazar.model.Producto;
import com.bazar.apibazar.model.Venta;
import com.bazar.apibazar.model.VentaProducto;
import com.bazar.apibazar.model.VentaProductoId;
import com.bazar.apibazar.repository.IClienteRepository;
import com.bazar.apibazar.repository.IProductoRepository;
import com.bazar.apibazar.repository.IVentaProductoRepository;
import com.bazar.apibazar.repository.IVentaRepository;
import jakarta.transaction.Transactional;
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
    
    /*Inyección de dependencia para la interfaz IventaProductoRepository que contiene todos los métodos necesarios
    para manejar la relación entre las tablas Venta y Producto*/
    @Autowired
    IVentaProductoRepository vpRepository;
    
    
    /*Método propio para saber cual es la cantitad total de productos que hay en una venta. La multiplicidad
    en los productos cuenta, es decir, un producto que esté repetido 3 veces, cuenta en el total de productos 
    como 3*/
    private int calcularCantidadDeProductosEnVenta(List<VentaProductoDto> listProductos){
        int totalProductos = 0;  

        for(VentaProductoDto objVP: listProductos){      
            totalProductos += objVP.getCantidad();   
        }
        
        return totalProductos;
    }
    
    //Método propiio para cálcular el total de una determinada venta
    private Double calcularTotal(List<VentaProductoDto> listProductos){
        Double total = 0.0;  
        
        for(VentaProductoDto objVP: listProductos){
            
            Producto objProducto = productoService.findProducto(objVP.getProductoId());
            
            if(objProducto != null){
                total += objProducto.getCosto()*objVP.getCantidad();
            }
        }
        
        return total;
    } 
    
    /*Método propio para elimiar todas las relaciones de una venta con cada uno de los productos asociados en la
    tabla intermedia VentaProducto*/
    private void eliminarRelacionVentaProducto(Venta objVenta){
        
        //Lista donde se van a guardar los productos con el stock actualizado
        List<Producto> listProductosNuevoStock = new ArrayList<>();
        
        /*Primero recorremos la lista VentaProducto de la venta*/
        for(VentaProducto objVP: objVenta.getListProductos()){ 
                
            //Encontramos arRelel Producto asociado a la relación
            Producto objProducto = objVP.getProducto();
                
            //Le devolvemos toda el stock que habia en la relación al producto correspondiente
            if(objProducto != null){
                objProducto.setCantidadDisponible(objProducto.getCantidadDisponible() + objVP.getCantidad());
                
                //Agregamos productoas a la lista de productos actualizados
                listProductosNuevoStock.add(objProducto);          
            }    
                
        }
        
        //Actualizamos los productos con un nuevo Stock
        productoService.saveAll(listProductosNuevoStock);
            
        /*Eliminamos todos los Productos de la venta, osea todos los registros relacionados con la venta
        en la tabla intermedia*/
        vpRepository.deleteAll(objVenta.getListProductos());
    }
    
    
    //Método propio para cear la relación entre una venta y cada uno de los productos con los que se va a relacionar
    @Transactional /*Con esta annotation si alguna parte del código falla, se revertirán los cambio y es como 
                    si nunca se hubiera ejecutado el método*/
    private void crearRelacionVentaProducto(List<VentaProductoDto> listProductos, Venta objVenta){
        

        /*Como es una relación ManyToMany entre Venta y Producto y la venta no tiene simples objetos Productos
        sino objetos DTO de la tabla intermedia VentaProducto, para poder hacer la relación lo primero que se
        debe hacer es un bucle for-each para recorrer todos los objetos VentaProductoDto que vienen en la venta*/
        for(VentaProductoDto objDto: listProductos){
            
            //Buscamos el producto relacionado con el id que vino en objDto
            Producto objProducto = productoService.findProducto(objDto.getProductoId());
            
            //Guardamos la cantidad de cada producto que se asignará a la venta en cuestión
            Integer cantidadProducto = objDto.getCantidad();
            
            /*Para que un productos que llega en objNuevo puedan ser agregado a la relación con la nueva venta,
            tiene que pasar dos filtros que demuestren que efectivamente está disponible, estos filtros se ven
            claramente en el condicional siguiente:*/
            if(objProducto != null && objProducto.getCantidadDisponible() >= cantidadProducto){
                
                /*Este solo es el sub-total entre una venta y un producto ya que el precio puede variar
                dependiendo de la cantidad que se aparte para la venta. No confundir con el total final de la 
                venta*/
                Double subTotal = objProducto.getCosto() * cantidadProducto;
                
                /*Una vez el producto pase los filtros llamamos al método que se encargue de crear el registro
                que representa la relación entre la venta y cada uno de los productos*/
                crearRegistroVentaProducto(objVenta, objProducto, cantidadProducto, subTotal);
                
            }
        }
       
    }
    
    
    /*Método propio para guardar un registro de la tabla intermedia VentaProducto que establece una relación
    Many-To-Many entre venta y Producto*/
    @Transactional /*Con esta annotation si alguna parte del código falla, se revertirán los cambio y es como 
                    si nunca se hubiera ejecutado el método*/
    private void crearRegistroVentaProducto(Venta objVenta, Producto objProducto, Integer cantidadProducto, Double subTotal){
        VentaProducto objRelacion = new VentaProducto();
            
        /*Creamos la PK compuesta del registro en cuestión la cual recibe como parámetro las PKs
        de las tablas relacionadas (Venta y Producto)*/
        objRelacion.setId(new VentaProductoId(objVenta.getIdVenta(), objProducto.getIdProducto()));
                
        //Agregamos el resto de datos de la relación
        objRelacion.setProducto(objProducto);
        objRelacion.setVenta(objVenta);
        objRelacion.setCantidad(cantidadProducto);
        /*Establecemos el subtotal de la relación, IMPORTANTE: este sólo es el total de la relación,
        es decir solo se tiene en cuenta el costo de un determinado producto y la cantidad que fue
        agregado a la venta, el TOTAL FINAL de la venta estará en la entidad Venta*/
        objRelacion.setSubTotalVenta(subTotal);
                
        //Le restamos la cantidad apartada del producto para la venta al Producto en cuestión
        objProducto.setCantidadDisponible(objProducto.getCantidadDisponible() - cantidadProducto);
                
        //Guardamos el registro de la relación en la tabla intermedia VentaProducto
        vpRepository.save(objRelacion);
    }
    
    
    @Override
    public List<GetVentaDto> getVentasSimples() {
        
        List<GetVentaDto> listVentas = new ArrayList<>();
        
        //Bucle for-each para recorrer todas las ventas registradas
        for(Venta objVenta: ventaRepository.findAll()){
            
            List<ProductoDeVentaDto> listProductos = new ArrayList<>();
            
            //Bucle for-each para recorrer todos los objetos VentaProducto de una determinada venta 
            for(VentaProducto objVP: objVenta.getListProductos()){
                
                Producto objProducto = objVP.getProducto();
                
                /*La lista se llena con objetos Dtos ya que hay unos datos que nos interesan y otros que no
                en la clase Producto*/
                listProductos.add(new ProductoDeVentaDto(objProducto.getIdProducto(), objProducto.getNombre(),
                        objProducto.getMarca(), objProducto.getCosto(), objVP.getCantidad(), objVP.getSubTotalVenta()));
            }
            
            listVentas.add(new GetVentaDto(objVenta.getIdVenta(), objVenta.getFechaVenta(), objVenta.getTotalVenta(),
                    objVenta.getCantidadTotalProductos(), listProductos, objVenta.getCliente()));
        }
        
        return listVentas;
    }

    @Override
    public GetVentaDto findVentaSimple(Long id) {
        Optional<Venta> objVentaOp = ventaRepository.findById(id);
        
        if(objVentaOp.isEmpty()){return null;}
        
        Venta objVenta = objVentaOp.get();
        
        List<ProductoDeVentaDto> listProductos = new ArrayList<>();

        //Bucle for-each para recorrer todos los objetos VentaParoducto de una determinada venta
        for(VentaProducto objVP: objVenta.getListProductos()){
            
            Producto objProducto = objVP.getProducto();
            
            /*La lista se llena con objetos Dtos ya que hay unos datos que nos interesan y otros que no
                en la clase Producto*/
            listProductos.add(new ProductoDeVentaDto(objProducto.getIdProducto(), objProducto.getNombre(),
                        objProducto.getMarca(), objProducto.getCosto(), objVP.getCantidad(), objVP.getSubTotalVenta()));
        }
        
        return new GetVentaDto(objVenta.getIdVenta(), objVenta.getFechaVenta(), objVenta.getTotalVenta(),
                objVenta.getCantidadTotalProductos(), listProductos, objVenta.getCliente());
  
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
    @Transactional /*Con esta annotation si alguna parte del código falla, se revertirán los cambio y es como 
                    si nunca se hubiera ejecutado el método*/
    public void saveVenta(VentaDto objNuevo) {
        Venta objVenta = new Venta();
        
        //Migramos datos del objeto Dto al objeto Venta
        objVenta.setFechaVenta(objNuevo.getFechaVenta());   
        objVenta.setCliente(objNuevo.getCliente());
        
        //Primero se guarda la venta sin los productos para obtener su id
        ventaRepository.save(objVenta);
        
        /*LLamamos al método que se encargue de crear las relaciones entre la venta y cada uno de los productos
        que llegaron como objetos VentaProductoDto en el objeto Dto de venta llamado objNuevo*/
        crearRelacionVentaProducto(objNuevo.getListProductos(), objVenta);
        
        //Ahora agregamos el total final de la venta a la entidad Venta
        objVenta.setTotalVenta(calcularTotal(objNuevo.getListProductos()));
        
        //Establecemos la cantidad total de todos los productos que hay en la venta
        objVenta.setCantidadTotalProductos(calcularCantidadDeProductosEnVenta(objNuevo.getListProductos()));
        
        //Guardamos nuevamente pero ahora con el total de la venta
        ventaRepository.save(objVenta);
    }

    @Override
    @Transactional /*Con esta annotation si alguna parte del código falla, se revertirán los cambio y es como 
                    si nunca se hubiera ejecutado el método*/
    public boolean deleteVenta(Long id) {
        
        Venta objVenta = findVenta(id);
        
        if(objVenta != null){
            
            /*LLamamos al método que se va a encargar de borrar las relaciones de la venta con cada uno de los
            productos que tenía asociado*/
            eliminarRelacionVentaProducto(objVenta);
            
            
            //Ahora si eliminamos la venta 
            ventaRepository.deleteById(id);
            
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional /*Con esta annotation si alguna parte del código falla, se revertirán los cambio y es como 
                    si nunca se hubiera ejecutado el método*/
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
        
        /*primero debemos borrar todas las relaciones que tenía la venta antigua con los productos, para asi 
        poder actualizar esas relaciones y que queden con los nurvos productos*/
        eliminarRelacionVentaProducto(objVenta);          
        
        /*Ahora llamamos al método que se encargue de crear las relaciones actualizadas entre la venta y cada 
        uno de los nuevos productos*/
        crearRelacionVentaProducto(objActualizado.getListProductos(), objVenta);
        
        //Ahora agregamos el total actualizado de la venta a la entidad Venta
        objVenta.setTotalVenta(calcularTotal(objActualizado.getListProductos()));
        
        //Establecemos la cantidad total de todos los productos que hay en la venta
        objVenta.setCantidadTotalProductos(calcularCantidadDeProductosEnVenta(objActualizado.getListProductos()));
        
        //Actualizamos la venta
        ventaRepository.save(objVenta);
        
        return objVenta;
    }

    @Override
    @Transactional /*Con esta annotation si alguna parte del código falla, se revertirán los cambio y es como 
                    si nunca se hubiera ejecutado el método*/
    public Venta patchVenta(Long id, VentaDto objDto) {
        Venta objVenta = findVenta(id);
        
        //Si no existe retornamos null
        if(objVenta == null){return objVenta;}
        
        
        //Actualizamos solo los datos que especifique el usuario en la request 
        if(objDto.getFechaVenta() != null){objVenta.setFechaVenta(objDto.getFechaVenta());}
        
        if(objDto.getCliente() != null){
 
            //Buscamos el cliente para agregarlo
            Cliente objCliente = clienteService.findCliente(objDto.getCliente().getIdCliente());
            
            //Lo agregamos
            objVenta.setCliente(objCliente);
        
        }
        
        if(!objDto.getListProductos().isEmpty()){     
            
            /*primero debemos borrar todas las relaciones que tenía la venta antigua con los productos, para asi 
            poder actualizar esas relaciones y que queden con los nuevos productos*/
            eliminarRelacionVentaProducto(objVenta);          
        
            /*Ahora llamamos al método que se encargue de crear las relaciones actualizadas entre la venta y cada 
            uno de los nuevos productos*/
            crearRelacionVentaProducto(objDto.getListProductos(), objVenta);
        
            //Ahora agregamos el total actualizado de la venta a la entidad Venta
            objVenta.setTotalVenta(calcularTotal(objDto.getListProductos()));
        
            //Establecemos la cantidad total de todos los productos que hay en la venta
            objVenta.setCantidadTotalProductos(calcularCantidadDeProductosEnVenta(objDto.getListProductos()));
        
        }
        
        //Actualizamos la venta
        ventaRepository.save(objVenta);
        
        return objVenta;
    }
    
    @Override
    @Transactional /*Con esta annotation si alguna parte del código falla, se revertirán los cambio y es como 
                    si nunca se hubiera ejecutado el método*/
    public Venta addProductosAVenta(Long id, List<VentaProductoDto> productosNuevos) {
        
        Venta objVenta = ventaRepository.findById(id).orElse(null);
        
        if(objVenta == null){return null;}
        
        //Método para crear las nuevas relaciones de venta con los nuevos productos      
        crearRelacionVentaProducto(productosNuevos, objVenta);
        
        //Actualizar venta 
        ventaRepository.save(objVenta);
        
        return objVenta;
    }
    
    
    @Override
    public List<Producto> productosDeVenta(Long id) {
        Venta objVenta = findVenta(id);
        
        //Si no se encuentra la venta, se retorna una lista vacía
        if(objVenta == null){return new ArrayList<>();}
        
        //Lista que almacenará todos los productos de la venta
        List<Producto> listProductos = new ArrayList<>();
                
        for(VentaProducto objVP: objVenta.getListProductos()){
            listProductos.add(objVP.getProducto());
        }
        
        return listProductos;
    }

    @Override
    public String ventasDelDia(LocalDate fechaVenta) {
        double total = 0;
        int contVentas= 0;
        
        for(Venta objVenta: getVentas()){
            
            //Comparamos la fecha de cada Venta a ver si coincide con la que viene en parametro
            if(objVenta.getFechaVenta().equals(fechaVenta)){
                total += objVenta.getTotalVenta();
                contVentas++;
                
                
            }
        }
        
        //Si confirmamos que al menos una venta coincide con la fecha, devolvemos lo pedido
        if(contVentas>0){
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
        
        //Le damos el valor de la primera venta de la lista para empezar a comparar
        Venta mayorVenta = listVentas.get(0);      
        
        //Buscamos la venta con mayor monto que va a terminar en "mayorVenta"
        for(Venta objVenta: listVentas){
            
            if(objVenta.getTotalVenta() > mayorVenta.getTotalVenta()){
                mayorVenta = objVenta;
            }
        }
        
        //Ahora migramos atributos a "VentaResumenDto" y retornamos
        VentaResumenDto mayorVentaDto = new VentaResumenDto();
        
        mayorVentaDto.setIdVenta(mayorVenta.getIdVenta());
        mayorVentaDto.setTotal(mayorVenta.getTotalVenta());
        mayorVentaDto.setCantProductos(mayorVenta.getCantidadTotalProductos());
        if(mayorVenta.getCliente() != null){
            mayorVentaDto.setNombreCliente(mayorVenta.getCliente().getNombre());
            mayorVentaDto.setApellidoCliente(mayorVenta.getCliente().getApellido());
        }
        
       
        return mayorVentaDto;  
        
    }

    

    
    
}
