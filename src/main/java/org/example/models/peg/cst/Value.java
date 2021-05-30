package org.example.models.peg.cst;

import java.util.Objects;

public final class Value extends CstNode {
    public final ValueKind kind;
    public final AcoTerminal data1;
    public final NonTerminal data2;
    public final OrderedChoice data3;

    public Value(ValueKind kind, AcoTerminal data1, NonTerminal data2, OrderedChoice data3) {
        Objects.requireNonNull(kind);
        assert (kind == ValueKind.VALUE_KIND1 && data1 != null && data2 == null && data3 == null) ||
                (kind == ValueKind.VALUE_KIND2 && data1 == null && data2 != null && data3 == null) ||
                (kind == ValueKind.VALUE_KIND3 && data1 == null && data2 == null && data3 != null);
        this.kind = kind;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        return kind == value.kind && Objects.equals(data1, value.data1) && Objects.equals(data2, value.data2) && Objects.equals(data3, value.data3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, data1, data2, data3);
    }

    @Override
    public String prettyPrint(int depth) {
        return null;
    }

    public enum ValueKind {
        VALUE_KIND1, VALUE_KIND2, VALUE_KIND3
    }
}
