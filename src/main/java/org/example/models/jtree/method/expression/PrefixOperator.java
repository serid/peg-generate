package org.example.models.jtree.method.expression;

import java.util.Objects;

public final class PrefixOperator extends Expression {
    public final String symbol;
    public final Expression e;

    public PrefixOperator(String symbol, Expression e) {
        Objects.requireNonNull(symbol);
        Objects.requireNonNull(e);
        this.symbol = symbol;
        this.e = e;
    }
}
