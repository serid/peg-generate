package org.example.models.jtree.method.statement;

import org.example.models.jtree.method.expression.Expression;
import org.example.models.jtree.type.TypeReference;

import java.util.Objects;

public final class Declaration extends Statement {
    public final TypeReference dataType;
    public final String name;
    public final Expression initializer;

    public Declaration(TypeReference dataType, String name, Expression initializer) {
        Objects.requireNonNull(dataType);
        Objects.requireNonNull(name);
        Objects.requireNonNull(initializer);
        this.dataType = dataType;
        this.name = name;
        this.initializer = initializer;
    }
}
