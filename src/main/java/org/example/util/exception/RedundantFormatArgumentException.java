package org.example.util.exception;

import java.util.Objects;

public class RedundantFormatArgumentException extends IllegalArgumentException {
    private final String s;

    /**
     * Constructs an instance of this class with the unmatched format
     * argument.
     *
     * @param s argument which does not have a corresponding specifier
     */
    public RedundantFormatArgumentException(String s) {
        Objects.requireNonNull(s);
        this.s = s;
    }

    /**
     * Returns the unmatched format argument.
     *
     * @return The unmatched format argument
     */
    public String getFormatArgument() {
        return s;
    }

    public String getMessage() {
        return "Format argument '" + s + "'";
    }
}
