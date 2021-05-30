package org.example.models.jtree.method.statement;

import org.example.models.jtree.method.expression.Expression;

import java.util.Objects;

public final class ExpressionStatement extends Statement {
    public final Expression expression;

    public ExpressionStatement(Expression expression) {
        Objects.requireNonNull(expression);
        this.expression = expression;
    }
}
