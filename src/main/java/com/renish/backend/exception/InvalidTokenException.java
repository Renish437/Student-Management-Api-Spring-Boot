package com.renish.backend.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String msg) {
        super(msg);
    }

    public InvalidTokenException(String msg, Throwable th) {
        super(msg, th);
    }
}
