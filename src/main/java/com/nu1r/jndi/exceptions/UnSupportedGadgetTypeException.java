package com.nu1r.jndi.exceptions;

public class UnSupportedGadgetTypeException extends RuntimeException {
    public UnSupportedGadgetTypeException(){ super();}
    public UnSupportedGadgetTypeException(String message){
        super(message);
    }
}
