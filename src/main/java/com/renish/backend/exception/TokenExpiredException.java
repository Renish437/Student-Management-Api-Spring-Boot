package com.renish.backend.exception;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String msg) {
        super(msg);
    }

    public TokenExpiredException(String msg, Throwable th) {
        super(msg, th);
    }
}
