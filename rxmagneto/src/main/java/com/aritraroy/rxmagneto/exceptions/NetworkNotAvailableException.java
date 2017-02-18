package com.aritraroy.rxmagneto.exceptions;

/**
 * Created by aritraroy on 18/02/17.
 */

/**
 * This exception is thrown by RxMagneto when the network connection is not available
 */
public class NetworkNotAvailableException extends Exception {

    public NetworkNotAvailableException(String message) {
        super(message);
    }
}
