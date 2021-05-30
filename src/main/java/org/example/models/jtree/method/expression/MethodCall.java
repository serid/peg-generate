package org.example.models.jtree.method.expression;

import java.util.Objects;

public final class MethodCall extends Expression {
    public final Expression receiver; // May be null (same as new This())!!!
    public final String name;
    public final Expression[] arguments;

    public MethodCall(Expression receiver, String name, Expression[] arguments) {
        Objects.requireNonNull(receiver);
        Objects.requireNonNull(name);
        Objects.requireNonNull(arguments);
        this.receiver = receiver;
        this.name = name;
        this.arguments = arguments;
    }

    public MethodCall(String name, Expression[] arguments) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(arguments);
        this.receiver = null;
        this.name = name;
        this.arguments = arguments;
    }
}
