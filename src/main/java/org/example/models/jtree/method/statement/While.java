package org.example.models.jtree.method.statement;

import org.example.models.jtree.method.expression.Expression;

import java.util.Objects;

public final class While extends Statement {
    public final Expression condition;
    public final Statement body;

    public While(Expression condition, Statement body) {
        Objects.requireNonNull(condition);
        Objects.requireNonNull(body);
        this.condition = condition;
        this.body = body;
    }
}
