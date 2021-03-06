package com.github.abryb.bsm;

class AppException extends Exception {
    AppException(String s, Exception e) {
        super(s, e);
    }

    AppException(Exception e) {
        super("Something went wrong. Totally unexpected error.", e);
    }
}
