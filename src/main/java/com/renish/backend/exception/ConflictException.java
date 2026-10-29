package com.renish.backend.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String msg) {
        super(msg);
    }

    public ConflictException(String msg, Throwable th) {
        super(msg, th);
    }
}
