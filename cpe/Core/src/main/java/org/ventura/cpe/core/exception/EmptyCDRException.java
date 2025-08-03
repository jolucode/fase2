package org.ventura.cpe.core.exception;

public class EmptyCDRException extends RuntimeException {

    public EmptyCDRException(String message) {
        super(message);
    }

    public EmptyCDRException(String message, Throwable cause) {
        super(message, cause);
    }
}
