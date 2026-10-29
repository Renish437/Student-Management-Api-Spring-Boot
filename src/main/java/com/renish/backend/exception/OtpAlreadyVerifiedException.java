package com.renish.backend.exception;

public class OtpAlreadyVerifiedException extends RuntimeException {
    public OtpAlreadyVerifiedException(String message) {
        super(message);
    }
}
