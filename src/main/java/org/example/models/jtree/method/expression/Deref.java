package org.example.models.jtree.method.expression;

import java.util.Objects;

public final class Deref extends Expression {
    public final Expression e;

    public Deref(Expression e) {
        Objects.requireNonNull(e);
        this.e = e;
    }
}
