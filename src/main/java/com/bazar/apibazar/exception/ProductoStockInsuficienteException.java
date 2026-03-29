package com.bazar.apibazar.exception;

//Excepción personalizada para indicar cuando el stock de un producto no es suficiente para cubrir la cantidad que se quiere comprar
public class ProductoStockInsuficienteException extends RuntimeException {

    //Recibimos mensaje de error en el parámetro del constructor y subimos hasta superclase Throwable
    public ProductoStockInsuficienteException(String message) {
        super(message);
    }
}
