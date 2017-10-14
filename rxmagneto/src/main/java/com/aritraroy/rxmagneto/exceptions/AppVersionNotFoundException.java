package com.aritraroy.rxmagneto.exceptions;

/**
 * Exception thrown when a proper app version is not available for the specified package
 * on Play Store. This typically happens when there is no version mentioned in Play Store page
 * itself and we get a message like "Varies with device".
 */
public class AppVersionNotFoundException extends Exception {
    public AppVersionNotFoundException(String message) {
        super(message);
    }
}