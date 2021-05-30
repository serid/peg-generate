package org.example.models.jtree.method.expression;

import java.util.Objects;

public final class StringConstant extends Expression {
    public final String data;

    public StringConstant(String data) {
        Objects.requireNonNull(data);
        this.data = data;
    }
}
