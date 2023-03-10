package com.nu1r.jndi.exceptions;

public class UnSupportedPayloadTypeException extends RuntimeException {
    public UnSupportedPayloadTypeException(){
        super();
    }
    public UnSupportedPayloadTypeException(String message){
        super(message);
    }
}
