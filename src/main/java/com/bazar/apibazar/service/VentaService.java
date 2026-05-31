package com.bazar.apibazar.service;

import com.bazar.apibazar.dto.cliente.ClienteResponseDto;
import com.bazar.apibazar.dto.venta.*;
import com.bazar.apibazar.exception.UnauthorizedOperationException;
import com.bazar.apibazar.exception.VentaCanceledException;
import com.bazar.apibazar.exception.VentaNotFoundException;
import com.bazar.apibazar.model.*;
import com.bazar.apibazar.repository.IVentaProductoRepository;
import com.bazar.apibazar.repository.IVentaRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService implements IVentaService{

    private final IVentaRepository ventaRepository;
    private final IProductoService productoService;
    private final IClienteService clienteService;
    private final IVentaProductoRepository vpRepository;

    public VentaService(IVentaRepository ventaRepository, ProductoService productoService, IClienteService clienteService, IVentaProductoRepository vpRepository) {
        this.ventaRepository = ventaRepository;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.vpRepository = vpRepository;
    }

    /**
     * Calcula el total acumulado de la venta
     * a partir de sus relaciones producto-venta
     */
    private Double calcularTotalVenta(Venta objVenta){
        if(objVenta == null){return 0.0;}
        
        Double totalFinal = 0.0;
        for(VentaProducto objVP: objVenta.getListProductos()){totalFinal += objVP.getSubTotalVenta();}
        
        return totalFinal;
    }

    private Integer calcularCantidadProductos(Venta objVenta){
        if(objVenta == null){return 0;}
        
        Integer totalProductos = 0;
        for(VentaProducto objVP: objVenta.getListProductos()){totalProductos += objVP.getCantidad();}
        
        return totalProductos;
    }

    /**
     * Crea las relaciones entre la venta y
     * los productos comprados
     */
    private void crearRelacionVentaProducto(List<VentaProductoDto> listProductos, Venta objVenta){

        for(VentaProductoDto objDto: listProductos){

            Producto objProducto = productoService.findProducto(objDto.getProductoId());

            Integer cantidadProducto = objDto.getCantidad();

            //Subtotal de este producto dentro de la venta
            Double subTotal = objProducto.getCosto() * cantidadProducto;

            crearRegistroVentaProducto(objVenta, objProducto, cantidadProducto, subTotal);

        }
        
        objVenta.setTotalVenta(calcularTotalVenta(objVenta));
        objVenta.setCantidadTotalProductos(calcularCantidadProductos(objVenta));
    }
    

    @Transactional
    private void crearRegistroVentaProducto(Venta objVenta, Producto objProducto, Integer cantidadProducto, Double subTotal){
        VentaProducto objRelacion = new VentaProducto();

        objRelacion.setProducto(objProducto);
        objRelacion.setVenta(objVenta);
        objRelacion.setCantidad(cantidadProducto);
        objRelacion.setSubTotalVenta(subTotal);

        objProducto.setCantidadDisponible(
                objProducto.getCantidadDisponible() - cantidadProducto
        );

        vpRepository.save(objRelacion);

        //Agrega la nueva relación a la lista de la venta
        objVenta.getListProductos().add(objRelacion);
    }
    
    /**
     * Construye DTO de venta para exponerla al cliente
     */
    private VentaResponseDto sacarVentaSimple(Venta objVenta){
        
        if(objVenta == null){return null;}

        //Lista de productos DTO
        List<ProductoDeVentaDto> listProductos = new ArrayList<>();

        for(VentaProducto objVP: objVenta.getListProductos()){
            
            Producto objProducto = objVP.getProducto();

            if(objProducto == null){continue; }

            listProductos.add(
                    new ProductoDeVentaDto(
                            objProducto.getIdProducto(),
                            objProducto.getNombre(),
                            objProducto.getMarca(),
                            objProducto.getCosto(),
                            objVP.getCantidad(),
                            objVP.getSubTotalVenta()
                    ));
        }
        
        return new VentaResponseDto(
                objVenta.getIdVenta(),
                objVenta.getFechaVenta(),
                objVenta.getTotalVenta(),
                objVenta.getCantidadTotalProductos(),
                listProductos,
                clienteService.sacarClienteSimple(
                        objVenta.getCliente()
                ),
                objVenta.getStatus()
        );
    }

    /**
     * Valida el estado de una venta
     * y verifica que no haya sido cancelada
     */
    private void validarEstadoVenta(Venta objVenta){
        if (objVenta.getStatus().equals(VentaStatus.CANCELED)){
            throw new VentaCanceledException("La venta con id: " + objVenta.getIdVenta() + " ha sido cancelada");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<VentaResponseDto> getVentasSimples() {
        List<VentaResponseDto> listVentas = new ArrayList<>();

        for(Venta objVenta: ventaRepository.findAll()){
            listVentas.add(
                    //Construye DTO con cada venta registrada
                    sacarVentaSimple(objVenta)
            );
        }
        
        return listVentas;
    }

    @Transactional(readOnly = true)
    @Override
    public VentaResponseDto findVentaSimple(Long id) {
        Venta objVenta = findVenta(id);
        return sacarVentaSimple(objVenta);
    }

    public  List<Venta> getVentas() {
        return ventaRepository.findAll();
    }

    /**
     * Busca venta por su id y lanza excepción NOT_FOUND
     * si no existe
     */
    public Venta findVenta(Long id) {
        Optional<Venta> objVenta = ventaRepository.findById(id);
        
        if(objVenta.isEmpty()){throw new VentaNotFoundException("No se encontró venta con id: " + id);}
        
        return objVenta.get();
    }

    @Transactional
    @Override
    public VentaResponseDto saveVenta(List<VentaProductoDto> listProductos) {

        productoService.validarStockProductos(listProductos);

        Venta objVenta = new Venta();

        //Inicialmente, la venta tiene estado PENDING
        objVenta.setStatus(VentaStatus.PENDING);

        objVenta.setFechaVenta(LocalDate.now());

        //Obtiene el cliente autenticado que realiza la compra
        Cliente objCliente = clienteService.findCliente(
                clienteService.getAuthenticatedClientId()
        );

        clienteService.validarDisponibilidadCliente(objCliente);

        objVenta.setCliente(objCliente);

        //La venta debe persistirse antes de crear relaciones producto-venta
        ventaRepository.save(objVenta);

        crearRelacionVentaProducto(listProductos, objVenta);

        objVenta.setStatus(VentaStatus.COMPLETED);

        return sacarVentaSimple(objVenta);
    }


    @Transactional
    @Override
    public void cancelVenta(Long id) {
        Venta objVenta = findVenta(id);

        //Cancelamos la venta asignándole es estado: CANCELED
        objVenta.setStatus(VentaStatus.CANCELED);
    }

    @Transactional
    @Override
    public VentaResponseDto addProductosAVenta(Long id, List<VentaProductoDto> productosNuevos) {
        productoService.validarStockProductos(productosNuevos);

        Venta objVenta = findVenta(id);

        Long clienteAuthenticatedId = clienteService.getAuthenticatedClientId();
        //Ownership: solo el cliente dueño de la venta puede modificarla
        if(!clienteAuthenticatedId.equals(objVenta.getCliente().getIdCliente())){
            throw new UnauthorizedOperationException("Usuario no autorizado para modificar la venta");
        }

        validarEstadoVenta(objVenta);

        clienteService.validarDisponibilidadCliente(objVenta.getCliente());

        //Productos nuevos en la venta
        List<VentaProductoDto> realesProductosNuevos = new ArrayList<>();

        for(VentaProductoDto objNuevo: productosNuevos){

            Producto objProducto = productoService.findProducto(objNuevo.getProductoId());

            //Valida la disponibilidad del producto
            if(objProducto == null || objProducto.getCantidadDisponible() < objNuevo.getCantidad()){continue;}

            objNuevo.setSubTotalVenta(objProducto.getCosto() * objNuevo.getCantidad());

            boolean yaExiste = false;
            for(VentaProducto objVP: objVenta.getListProductos()){

                //Valida si alguno de los productos nuevos ya existe en la venta
                if(objVP.getProducto().getIdProducto().equals(objProducto.getIdProducto())){
                    yaExiste = true;
                }

                /**
                 * Si ya existe el producto en la venta,
                 * se actualiza la cantidad comprada y el subtotal de este
                 */
                if(yaExiste){

                    objVP.setCantidad(objVP.getCantidad() + objNuevo.getCantidad());

                    objVP.setSubTotalVenta(objVP.getSubTotalVenta() + objNuevo.getSubTotalVenta());

                    //Descuenta stock
                    objProducto.setCantidadDisponible(objProducto.getCantidadDisponible() - objNuevo.getCantidad());

                    productoService.saveProducto(objProducto);

                    vpRepository.save(objVP);

                    break;
                }

            }

            //Si el producto no está en la venta, se agrega a los productos nuevos
            if(!yaExiste){realesProductosNuevos.add(objNuevo);}
        }

        crearRelacionVentaProducto(realesProductosNuevos, objVenta);

        objVenta.setTotalVenta(calcularTotalVenta(objVenta));
        objVenta.setCantidadTotalProductos(calcularCantidadProductos(objVenta));
        
        return sacarVentaSimple(objVenta);
    }

    @Transactional
    @Override
    public VentaResponseDto deleteProductosDeVenta(Long id, List<VentaProductoDto> productosEliminados) {
        Venta objVenta = findVenta(id);

        Long clienteAuthenticatedId = clienteService.getAuthenticatedClientId();
        //Solo se modifican las ventas pertenecientes al cliente autenticado
        if(!clienteAuthenticatedId.equals(objVenta.getCliente().getIdCliente())){
            throw new UnauthorizedOperationException("Usuario no autorizado para modificar la venta");
        }

        validarEstadoVenta(objVenta);
        clienteService.validarDisponibilidadCliente(objVenta.getCliente());

        List<VentaProducto> realesProductosEliminados = new ArrayList<>();

        for(VentaProductoDto objBorrar: productosEliminados){
            //Obtiene producto a eliminar
            Producto objProducto = productoService.findProducto(objBorrar.getProductoId());
            
            if(objProducto == null){continue;}

            objBorrar.setSubTotalVenta(
                    objProducto.getCosto() * objBorrar.getCantidad()
            );

            for(VentaProducto objVP: objVenta.getListProductos()){
                
                //Comparamos cada producto de la venta con el producto a eliminar
                if(objVP.getProducto().getIdProducto().equals(objProducto.getIdProducto())) {

                    //Valida que la cantidad a eliminar no sobrepase la comprada
                    if(objBorrar.getCantidad() > objVP.getCantidad()){break;}

                    if(objBorrar.getCantidad() < objVP.getCantidad()) {

                        //Descuenta cantidad
                        objVP.setCantidad(
                                objVP.getCantidad() - objBorrar.getCantidad()
                        );

                        //Descuenta subTotal
                        objVP.setSubTotalVenta(
                                objVP.getSubTotalVenta() - objBorrar.getSubTotalVenta()
                        );

                        //Repone la cantidad eliminada de la venta al stock del producto
                        objProducto.setCantidadDisponible(
                                objProducto.getCantidadDisponible() + objVP.getCantidad()
                        );
                        productoService.saveProducto(objProducto);

                    /**
                     * Si se manda a eliminar de la venta toda la cantidad
                     * del producto, se elimina la relación completa
                     */
                    }else {
                        //Las relaciones a eliminar se acumulan para evitar modificar la colección durante el recorrido
                        realesProductosEliminados.add(objVP);

                        objVenta.getListProductos().remove(objVP);

                        //Repone stock eliminado de la venta al producto
                        objProducto.setCantidadDisponible(objProducto.getCantidadDisponible() + objVP.getCantidad());
                        productoService.saveProducto(objProducto);
                        
                        break;
                    }
                         
                }
            }
        }

        vpRepository.deleteAll(realesProductosEliminados);
        
        //recalcula total y cantidad comprada de la venta
        objVenta.setTotalVenta(calcularTotalVenta(objVenta));
        objVenta.setCantidadTotalProductos(calcularCantidadProductos(objVenta));
        
        return sacarVentaSimple(objVenta);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Producto> productosDeVenta(Long id) {
        Venta objVenta = findVenta(id);

        List<Producto> listProductos = new ArrayList<>();
                
        for(VentaProducto objVP: objVenta.getListProductos()){
            listProductos.add(objVP.getProducto());
        }
        
        return listProductos;
    }

    @Transactional(readOnly = true)
    @Override
    public String ventasDelDia(LocalDate fechaVenta) {
        double total = 0;
        int contVentas= 0;
        
        for(Venta objVenta: getVentas()){
            
            //Busca coincidencias de fecha entre las ventas
            if(objVenta.getFechaVenta().equals(fechaVenta)){
                total += objVenta.getTotalVenta();
                contVentas++;
                
                
            }
        }

        if(contVentas>0){
            return "El total de las ventas del día " + fechaVenta + " es: $" + total + ".\n"
                    + "Y se ha hecho un total de " + contVentas + " venta(s).";
        }

        throw new VentaNotFoundException("No se encontró venta con fecha: " + fechaVenta);
        
    }

    @Transactional(readOnly = true)
    @Override
    public VentaResumenDto findMayorVenta() {

        List<Venta> listVentas = getVentas();
        if(listVentas.isEmpty()){throw new VentaNotFoundException("No hay ventas registradas");}

        //Se empieza a comparar por la priemera venta
        Venta mayorVenta = listVentas.get(0);      

        for(Venta objVenta: listVentas){
            
            if(objVenta.getTotalVenta() > mayorVenta.getTotalVenta()){
                mayorVenta = objVenta;
            }
        }

        ClienteResponseDto objCliente = clienteService.sacarClienteSimple(
                mayorVenta.getCliente()
        );

        return new VentaResumenDto(
                mayorVenta.getIdVenta(),
                mayorVenta.getTotalVenta(),
                mayorVenta.getCantidadTotalProductos(),
                objCliente.nombre(), objCliente.apellido()

        );
        
    }
    
}
