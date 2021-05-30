package org.example.models.jtree.method.expression;

import java.util.Objects;

public final class GetIdentValue extends Expression {
    public final String name;

    public GetIdentValue(String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }
}
