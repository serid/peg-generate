package org.example.impl.peg;

// TODO: add source span information for the error
public class PegException extends RuntimeException {
    public PegException() {
        super();
    }

    public PegException(String message) {
        super(message);
    }

    public PegException(String message, Throwable cause) {
        super(message, cause);
    }

    public PegException(Throwable cause) {
        super(cause);
    }

    protected PegException(String message, Throwable cause,
                           boolean enableSuppression,
                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
