package org.example.models.jtree.method.expression;

import java.util.Objects;

public final class BinaryOperator extends Expression {
    public final String symbol;
    public final Expression e1;
    public final Expression e2;

    public BinaryOperator(String symbol, Expression e1, Expression e2) {
        Objects.requireNonNull(symbol);
        Objects.requireNonNull(e1);
        Objects.requireNonNull(e2);
        this.symbol = symbol;
        this.e1 = e1;
        this.e2 = e2;
    }
}
