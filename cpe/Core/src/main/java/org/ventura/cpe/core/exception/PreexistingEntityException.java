package org.ventura.cpe.core.exception;

public class PreexistingEntityException extends Exception {

    public PreexistingEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreexistingEntityException(String message) {
        super(message);
    }
}
