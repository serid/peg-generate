package org.example.models.jtree.method.expression;

import java.util.Objects;

public final class GetField extends Expression {
    public final Expression e;
    public final String fieldName;

    public GetField(Expression e, String fieldName) {
        Objects.requireNonNull(e);
        Objects.requireNonNull(fieldName);
        this.e = e;
        this.fieldName = fieldName;
    }
}
