package org.example.models.jtree.method.statement;

import org.example.models.jtree.method.expression.Expression;

import java.util.Objects;

public final class Return extends Statement {
    public final Expression value;

    public Return(Expression value) {
        Objects.requireNonNull(value);
        this.value = value;
    }
}
