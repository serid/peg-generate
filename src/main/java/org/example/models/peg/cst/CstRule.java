package org.example.models.peg.cst;

import java.util.Objects;

public final class CstRule extends CstNode {
    public final NonTerminal data1;
    public final OrderedChoice data2;

    public CstRule(NonTerminal data1, OrderedChoice data2) {
        Objects.requireNonNull(data1);
        Objects.requireNonNull(data2);
        this.data1 = data1;
        this.data2 = data2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CstRule rule = (CstRule) o;
        return data1.equals(rule.data1) && data2.equals(rule.data2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data1, data2);
    }

    @Override
    public String prettyPrint(int depth) {
        return null;
    }
}
