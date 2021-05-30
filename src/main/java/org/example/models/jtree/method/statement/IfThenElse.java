package org.example.models.jtree.method.statement;

import org.example.models.jtree.method.expression.Expression;

import java.util.Objects;

public final class IfThenElse extends Statement {
    public final Expression condition;
    public final Statement on_true;
    public final Statement on_false;

    public IfThenElse(Expression condition, Statement on_true, Statement on_false) {
        Objects.requireNonNull(condition);
        Objects.requireNonNull(on_true);
        Objects.requireNonNull(on_false);
        this.condition = condition;
        this.on_true = on_true;
        this.on_false = on_false;
    }
}
