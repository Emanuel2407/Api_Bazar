package com.bazar.apibazar.exception;

//Excepción personalizada para indicar cuando una venta no fue encontrada
public class VentaNotFoundException extends RuntimeException{

    //Recibimos el mensaje de la excepción como parámetro de la clase
    public VentaNotFoundException(String message){
        /*Subimos el mensaje de la excepción a RuntimeException y de ahí sigue subiendo hasta llegar a la superclase
          Throwable donde expone por medio del método getMessage()*/
        super(message);
    }
}
