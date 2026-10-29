package com.renish.backend.exception;

public class ResourceNotFoundException extends RuntimeException {
     
    public ResourceNotFoundException(String msg){
        super(msg);
    }
    public ResourceNotFoundException(String msg,Throwable th){
        super(msg,th);
    }
}
