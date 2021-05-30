package org.example.util.exception;

public class UnimplementedError extends Error {
    public UnimplementedError() {
        super();
    }

    public UnimplementedError(String message) {
        super(message);
    }

    protected UnimplementedError(String message, Throwable cause,
                                 boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
