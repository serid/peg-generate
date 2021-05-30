package org.example.models.jtree.method.statement;

import org.example.models.jtree.method.expression.Expression;

import java.util.Objects;

public final class For extends Statement {
    public final Declaration declaration;
    public final Expression condition;
    public final Expression postaction;
    public final Statement body;

    public For(Declaration declaration, Expression condition, Expression postaction, Statement body) {
        Objects.requireNonNull(declaration);
        Objects.requireNonNull(condition);
        Objects.requireNonNull(postaction);
        Objects.requireNonNull(body);
        this.declaration = declaration;
        this.condition = condition;
        this.postaction = postaction;
        this.body = body;
    }
}
