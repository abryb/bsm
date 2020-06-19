package com.github.abryb.bsm;

public class InsufficientPasswordException extends AppException {
    public InsufficientPasswordException(String message) {
        super(message, null);
    }
}
