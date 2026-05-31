package com.bazar.apibazar.exception;

/**
 * Indica que el stock de un producto no es suficiente
 * para cubrir la cantidad que se desea comprar.
 */
public class ProductoStockInsuficienteException extends RuntimeException {

    public ProductoStockInsuficienteException(String message) {
        super(message);
    }
}
