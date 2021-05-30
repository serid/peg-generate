package org.example.models.jtree.method.statement;

import java.util.Objects;

public final class Block extends Statement {
    public static final Block empty = new Block(new Statement[]{});

    public final Statement[] statements;

    public Block(Statement[] statements) {
        Objects.requireNonNull(statements);
        this.statements = statements;
    }

    public Block(Statement statement) {
        Objects.requireNonNull(statement);
        this.statements = new Statement[]{statement};
    }
}
