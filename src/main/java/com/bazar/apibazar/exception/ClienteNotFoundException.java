package com.bazar.apibazar.exception;

//Excepción personalizada para indicar que un cliente no fue encontrado
public class ClienteNotFoundException extends RuntimeException{

    //Recibimos el mensaje de la excepción como parámetro de la clase
    public ClienteNotFoundException(String message){
        /*Subimos el mensaje de la excepción a RuntimeException y de ahí sigue subiendo hasta llegar a la superclase
          Throwable donde expone por medio del método getMessage()*/
        super(message);
    }
}
