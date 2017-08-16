package com.aritraroy.rxmagneto.exceptions;

/**
 * Created by aritraroy on 18/02/17.
 *
 * Generic exception thrown by {@link com.aritraroy.rxmagneto.RxMagnetoInternal}.
 */
public class RxMagnetoException extends Exception {

    int errorCode;

    public RxMagnetoException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
