package com.aritraroy.rxmagneto.exceptions;

/**
 * Created by aa on 17/03/17.
 *
 * Exception thrown when proper app version is not found for the specified package on Play Store
 */

public class AppVersionNotFoundException extends Exception {
    public AppVersionNotFoundException(String message) {
        super(message);
    }
}