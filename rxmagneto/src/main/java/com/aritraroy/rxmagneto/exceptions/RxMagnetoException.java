package com.aritraroy.rxmagneto.exceptions;

/**
 * Generic exception thrown by RxMagneto containing an {@code errorCode} specifying the exact
 * error and a message describing it in detail
 */
public class RxMagnetoException extends Exception {

    private final int errorCode;

    public RxMagnetoException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
