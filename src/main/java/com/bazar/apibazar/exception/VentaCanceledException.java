package com.bazar.apibazar.exception;

//Excepción personalizada para indicar que una venta ha sido cancelada cuando se quiere hacer una operación o modificación sobre ella
public class VentaCanceledException extends RuntimeException {
    public VentaCanceledException(String message) {
        super(message);
    }
}
