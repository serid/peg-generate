package org.example.util.exception;

public class UnreachableReachedError extends Error {
    public UnreachableReachedError() {
        super();
    }

    public UnreachableReachedError(String message) {
        super(message);
    }

    protected UnreachableReachedError(String message, Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
