package com.aritraroy.rxmagneto.exceptions;

/**
 * Exception thrown when the network connection is not available for the device
 */
public class NetworkNotAvailableException extends Exception {
    public NetworkNotAvailableException(String message) {
        super(message);
    }
}
