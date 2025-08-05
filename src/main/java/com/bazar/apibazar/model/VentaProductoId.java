package com.bazar.apibazar.model;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/*Esta clase va a representar la clave primaria compuesta de nuestra tabla intermedia, por esto debe usarser la 
annotation "@Embeddable" e implementar la interfaz Serializable*/
@Embeddable
@Getter  @Setter
public class VentaProductoId implements Serializable{
    
    /*Como es compueta, necesita tener referencias a las dos claves primarias de las entidades relacionadas, 
    en este caso Venta y Producto*/
    private Long ventaId;
    private Long productoId;
    
    //Contructor vacío requerido por JPA
    public VentaProductoId() {
    }

    public VentaProductoId(Long ventaId, Long productoId) {
        this.ventaId = ventaId;
        this.productoId = productoId;
    }

    
    /*Los siguientes métodos son sobreescritos de la superclase general Object, y son interpretados y usados
    por Jpa-Hibernate para que no haya una clave compuesta repetida en la Entida intermedia VentaProducto*/
    
    //Método para comprobar que dos objetos son iguales
    @Override
    public boolean equals(Object o){
        //this es el objeto que llamó al método
        if(this == o){return true;}
        
        //instanceof es un operador que devuelve true si un objeto es instancia de una clase
        if(!(o instanceof VentaProductoId)){return false;}
        
        //Cast del objeto generico tipo Objects a objto de VentaProductoId
        VentaProductoId that = (VentaProductoId) o;
        
        return Objects.equals(ventaId, that.ventaId) &&
                Objects.equals(productoId, that.productoId);
        
    }
    
    //Método que devuelve un numero único para cada llave primaria compuesta
    @Override
    public int hashCode(){
        return Objects.hash(ventaId, productoId);
    }
    
}
