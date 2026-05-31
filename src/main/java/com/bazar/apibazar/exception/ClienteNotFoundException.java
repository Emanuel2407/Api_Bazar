package com.bazar.apibazar.exception;

public class ClienteNotFoundException extends RuntimeException{

    public ClienteNotFoundException(String message){
        super(message);
    }
}
