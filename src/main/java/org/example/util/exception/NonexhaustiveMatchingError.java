package org.example.util.exception;

public class NonexhaustiveMatchingError extends Error {
    protected NonexhaustiveMatchingError(String message, Throwable cause,
                                         boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public <T> NonexhaustiveMatchingError(T value) {
        super(String.format("unhandled case (%s) in type %s", value, value.getClass().getName()));
    }
}
