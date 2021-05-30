package org.example.models.jtree.method.expression;

import org.example.util.Lazy;

import java.util.Objects;

public final class NewCall extends Expression {
    public final String typeName;
    public final Lazy<Expression[]> arguments;

    public NewCall(String typeName, Lazy<Expression[]> arguments) {
        Objects.requireNonNull(typeName);
        Objects.requireNonNull(arguments);
        this.typeName = typeName;
        this.arguments = arguments;
    }
}
