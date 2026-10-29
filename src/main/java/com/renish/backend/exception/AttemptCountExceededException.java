package com.renish.backend.exception;

public class AttemptCountExceededException extends RuntimeException {
    public AttemptCountExceededException(String message) {
        super(message);
    }
}
