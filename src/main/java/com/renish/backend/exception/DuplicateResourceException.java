package com.renish.backend.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String msg){
        super(msg);
    }
    public DuplicateResourceException(String msg,Throwable th){
        super(msg,th);
    }
}
