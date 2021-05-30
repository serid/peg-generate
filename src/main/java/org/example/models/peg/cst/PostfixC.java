package org.example.models.peg.cst;

import java.util.Objects;

public final class PostfixC extends CstNode {
    public final PostfixCKind kind;

    public PostfixC(PostfixCKind kind) {
        Objects.requireNonNull(kind);
        this.kind = kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostfixC postfixC = (PostfixC) o;
        return kind == postfixC.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind);
    }

    @Override
    public String prettyPrint(int depth) {
        return null;
    }

    public enum PostfixCKind {
        POSTFIX_C_KIND1, POSTFIX_C_KIND2, POSTFIX_C_KIND3
    }
}
