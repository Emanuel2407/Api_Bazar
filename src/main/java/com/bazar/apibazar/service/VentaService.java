package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.ClienteDeVentaDto;
import com.bazar.apibazar.dto.VentaSimpleDto;
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
    
    //Inyección de dependencia para ClienteService
    @Autowired
    IClienteRepository clienteRepository;
    
    /*Inyección de dependencia para la interfaz IventaProductoRepository que contiene todos los métodos necesarios
    para manejar la relación entre las tablas Venta y Producto*/
    @Autowired
    IVentaProductoRepository vpRepository;
    
    /*Método propio para calcular el total final de una venta a partir de cada uno de los subTtotales de 
    sus productos*/
    private Double calcularTotalVenta(Venta objVenta){
        if(objVenta == null){return 0.0;}
        
        Double totalFinal = 0.0;
        for(VentaProducto objVP: objVenta.getListProductos()){totalFinal += objVP.getSubTotalVenta();}
        
        return totalFinal;
    }
    
    //Método propio para calcular el total de productos de una venta
    private Integer calcularCantidadProductos(Venta objVenta){
        if(objVenta == null){return 0;}
        
        Integer totalProductos = 0;
        for(VentaProducto objVP: objVenta.getListProductos()){totalProductos += objVP.getCantidad();}
        
        return totalProductos;
    }
    
    //Método propio para encontrar el cliente de una determinada Venta
    private ClienteDeVentaDto buscarClienteDeVenta(Long idVenta){
        
        //Recorrer todos los clientes registrados
        for(Cliente objCliente: clienteRepository.findAll()){
            
            //Recorrer las ventas de cada cliente
            for(Venta objVenta: objCliente.getListVentas()){
                
                //Si entontramos el cliente lo retornamos
                if(objVenta.getIdVenta() == idVenta){return new ClienteDeVentaDto(objCliente.getNombre(),
                        objCliente.getApellido(), objCliente.getDocumento());}
            }
        }
        
        return null;
        
    }
    

    /*Método propio para elimiar todas las relaciones de una venta con cada uno de los productos asociados en la
    tabla intermedia VentaProducto*/
    private void eliminarRelacionVentaProducto(Venta objVenta){
        
        //Lista donde se van a guardar los productos que van a ser elimados de la venta con el stock actualizado
        List<Producto> listProductosNuevoStock = new ArrayList<>();
        
        /*Primero recorremos la lista VentaProducto de la venta*/
        for(VentaProducto objVP: objVenta.getListProductos()){ 
                
            //Encontramos cada Producto asociado a la relación
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
        
        /*Limpiamos la lista de productos del objeto objVenta para evitar que sean buscados cuando se 
        actualice la venta ya que como fueron eliminados, no serán encontrados y lanzará error*/
        objVenta.getListProductos().clear();
      
    }
    
    
    //Método propio para cear la relación entre una venta y cada uno de los productos con los que se va a relacionar
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
        
        objVenta.setTotalVenta(calcularTotalVenta(objVenta));
        objVenta.setCantidadTotalProductos(calcularCantidadProductos(objVenta));
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
        
        /*Vamos agregando también cada objeto de la relación al objeto objVenta en memoria para no tener
        dificultades al momento de llamar a este objeto que está en caché*/
        objVenta.getListProductos().add(objRelacion);
    }
    
    //Método propio para cambiar los objetos VentaProducto de las ventas por objetos Producto simples
    private VentaSimpleDto sacarVentaSimple(Venta objVenta){
        
        if(objVenta == null){return null;}
        
        //Lista que va a contener los productos simplificados de la venta
        List<ProductoDeVentaDto> listProductos = new ArrayList<>();

        //Bucle for-each para recorrer todos los objetos VentaParoducto de la venta
        for(VentaProducto objVP: objVenta.getListProductos()){
            
            Producto objProducto = objVP.getProducto();
            
            //Si el producto es null, no se agrega a la lista
            if(objProducto == null){continue; }
            
            /*La lista se llena con objetos Dtos ya que hay unos datos que nos interesan y otros que no
            en la clase Producto.*/
            listProductos.add(new ProductoDeVentaDto(objProducto.getIdProducto(), objProducto.getNombre(),
                        objProducto.getMarca(), objProducto.getCosto(), objVP.getCantidad(), objVP.getSubTotalVenta()));
        }
        
        return new VentaSimpleDto(objVenta.getIdVenta(), objVenta.getFechaVenta(), objVenta.getTotalVenta(),
                objVenta.getCantidadTotalProductos(), listProductos, buscarClienteDeVenta(objVenta.getIdVenta()));
    }
    
    
    @Override
    public List<VentaSimpleDto> getVentasSimples() {
        
        List<VentaSimpleDto> listVentas = new ArrayList<>();
        
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
            
            listVentas.add(new VentaSimpleDto(objVenta.getIdVenta(), objVenta.getFechaVenta(), objVenta.getTotalVenta(),
                    objVenta.getCantidadTotalProductos(), listProductos, buscarClienteDeVenta(objVenta.getIdVenta())));
        }
        
        return listVentas;
    }

    @Override
    public VentaSimpleDto findVentaSimple(Long id) {
        Venta objVenta = findVenta(id);
        
        if(objVenta == null){return null;}
        
        /*e llama al método que se encargue de cambiar todos los productos VentaProducto de una Venta a 
        productos simples y así evitar recursividad infinita*/
        return sacarVentaSimple(objVenta);
    }
    
    
    //Método propio para buscar todas las ventas con listas VentaProducto
    private  List<Venta> getVentas() {
        return ventaRepository.findAll();
    }

    //Método propio para buscar una venta en especifico que tenga sus respectivos objetos VentaProducto
    private Venta findVenta(Long id) {
        Optional<Venta> objVenta = ventaRepository.findById(id);
        
        if(objVenta.isEmpty()){return null;}
        
        return objVenta.get();
    }

    @Override
    public void saveVenta(VentaDto objNuevo) {
        Venta objVenta = new Venta();
        
        //Migramos datos del objeto Dto al objeto Venta
        objVenta.setFechaVenta(objNuevo.getFechaVenta());   
        
        //Primero se guarda la venta sin los productos para obtener su id
        ventaRepository.save(objVenta);
        
        /*LLamamos al método que se encargue de crear las relaciones entre la venta y cada uno de los productos
        que llegaron como objetos VentaProductoDto en el objeto Dto de venta llamado objNuevo*/
        crearRelacionVentaProducto(objNuevo.getListProductos(), objVenta);
        
    
        //Guardamos nuevamente pero ahora con el total de la venta
        ventaRepository.save(objVenta);
    }

    @Override
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
    public VentaSimpleDto updateVenta(Long id, VentaDto objActualizado) {
        Venta objVenta = findVenta(id);
        
        //Si no existe retornamos null
        if(objVenta == null){return null;}
        
        //Actualizamos datos de la venta en cuestion
        objVenta.setFechaVenta(objActualizado.getFechaVenta());

        /*primero debemos borrar todas las relaciones que tenía la venta antigua con los productos, para asi 
        poder actualizar esas relaciones y que queden con los nuevos productos*/
        eliminarRelacionVentaProducto(objVenta);          
        
        /*Ahora llamamos al método que se encargue de crear las relaciones actualizadas entre la venta y cada 
        uno de los nuevos productos*/
        crearRelacionVentaProducto(objActualizado.getListProductos(), objVenta);
        
        
        //Actualizamos la venta
        ventaRepository.save(objVenta);
        
        return sacarVentaSimple(objVenta);
    }

    @Override
    public VentaSimpleDto patchVenta(Long id, VentaDto objDto) {
        Venta objVenta = findVenta(id);
        
        //Si no existe retornamos null
        if(objVenta == null){return null;}
        
        //Actualizamos fecha de la venta 
        if(objDto.getFechaVenta() != null){objVenta.setFechaVenta(objDto.getFechaVenta());}
        
        if(!objDto.getListProductos().isEmpty()){     
            
            /*primero debemos borrar todas las relaciones que tenía la venta antigua con los productos, para asi 
            poder actualizar esas relaciones y que queden con los nuevos productos*/
            eliminarRelacionVentaProducto(objVenta);          
            
            /*Ahora llamamos al método que se encargue de crear las relaciones actualizadas entre la venta y cada 
            uno de los nuevos productos*/
            crearRelacionVentaProducto(objDto.getListProductos(), objVenta);
        
        
        }
        
        //Actualizamos la venta
        ventaRepository.save(objVenta);
        
        return sacarVentaSimple(objVenta);
    }
    
    @Override
    public VentaSimpleDto addProductosAVenta(Long id, List<VentaProductoDto> productosNuevos) {
        
        //Buscamos venta a realizar la insercción de productos
        Venta objVenta = findVenta(id);
        
        if(objVenta == null){return null;}
        
        /*Si bien los productos que vamos a agregar son los que llegan en "productosNuevos", debemos diferenciar
        los productos que ya estaban antes en la venta ya que a estos solo se le sumará la cantidad correspondiente
        a la cantidad comprada y con los que realmente son nuevos se van a crear nuevas relaciones con la venta*/
        List<VentaProductoDto> realesProductosNuevos = new ArrayList<>();
        
        //Recorremos cada uno de los productos que se mandan desde el cliente
        for(VentaProductoDto objNuevo: productosNuevos){
            
            Producto objProducto = productoService.findProducto(objNuevo.getProductoId());
                
            //Si el producto asociado a la relación es null o su cantidad es insuficiente, saltaremos al siguiente
            if(objProducto == null || objProducto.getCantidadDisponible() < objNuevo.getCantidad()){continue;}
            
            /*Como hasta el momento no hemos creado una relación entre la venta y objNuevo, no se le ha 
            asignado un total a la relación, por ende los hacemos de forma manual*/
            objNuevo.setSubTotalVenta(objProducto.getCosto() * objNuevo.getCantidad());
            
            //Con esto determinaremos si el objeto realmete existía o no en la venta
            boolean yaExiste = false; 
            
            //Ahora reacorremos todos los objetos o productos de la venta 
            for(VentaProducto objVP: objVenta.getListProductos()){
                 
                /*Vamos comparando cada objeto de la venta con objNuevo, en caso de que alguno sea igual a
                objNuevo, el campo "yaExiste" será true*/
                if(objVP.getProducto().getIdProducto() == objProducto.getIdProducto()){yaExiste = true;}  
                
                /*Si en alguna vuelta del bucle "yaExiste" es true, confirmamos que efectivamente la relació
                entre objNuevo y la venta ya existe, osea que solo hay que hacerle ciertas modificaciones*/
                if(yaExiste){
                    
                    //Acualizamos la cantidad de la relación
                    objVP.setCantidad(objVP.getCantidad() + objNuevo.getCantidad());
                    
                    //Le actualizamos el total a la relación sumando al que ya tenia el nuevo calculado anteriormente
                    objVP.setSubTotalVenta(objVP.getSubTotalVenta() + objNuevo.getSubTotalVenta());
                    
                    //Ahora le debemos descontar la nueva cantidad comprada al producto de la relación
                    objProducto.setCantidadDisponible(objProducto.getCantidadDisponible() - objNuevo.getCantidad());
                    
                    //Actualizamos producto en bd
                    productoService.saveProducto(objProducto);
                    
                    //Actualizamos relación en bd
                    vpRepository.save(objVP);
                    
                    /*Ya no es necesario seguir comparando objNuevo con los demas productos de objVenta asi que
                    salimos de este bucle para que objNuevo avanze al siguiente*/
                    break;
                }
                
            }
            
            /*Ahora, si cuando termine el bucle, "yaExiste" sigue en false, confirmamos que aún no existe esa 
            relación y agregamos el producto a la nueva lista para crearla*/
            if(!yaExiste){realesProductosNuevos.add(objNuevo);}
        }        
 
        //Método para crear las nuevas relaciones de venta con los nuevos productos que no tenian relación 
        crearRelacionVentaProducto(realesProductosNuevos, objVenta);
        
        //recalculamos el total y la cantidad de productos en la venta, ahora con los productos nuevos
        objVenta.setTotalVenta(calcularTotalVenta(objVenta));
        objVenta.setCantidadTotalProductos(calcularCantidadProductos(objVenta));
        
        //Actualizar venta 
        ventaRepository.save(objVenta);
        
        return sacarVentaSimple(objVenta);
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
        
        //Buscamo el cliente de la Mayor venta
        ClienteDeVentaDto objCliente = buscarClienteDeVenta(mayorVenta.getIdVenta());
        
        if(objCliente != null){
            mayorVentaDto.setNombreCliente(objCliente.getNombre());
            mayorVentaDto.setApellidoCliente(objCliente.getApellido());
        }
        
       
        return mayorVentaDto;  
        
    }

    

    
    
}
