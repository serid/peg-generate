package org.example.models.peg.cst;

import java.util.Objects;

public final class Prefix extends CstNode {
    public final PrefixKind kind;
    public final Prefix data1;
    public final Prefix data2;
    public final Value data3;

    public Prefix(PrefixKind kind, Prefix data1, Prefix data2, Value data3) {
        Objects.requireNonNull(kind);
        assert (kind == PrefixKind.PREFIX_KIND1 && data1 != null && data2 == null && data3 == null) ||
                (kind == PrefixKind.PREFIX_KIND2 && data1 == null && data2 != null && data3 == null) ||
                (kind == PrefixKind.PREFIX_KIND3 && data1 == null && data2 == null && data3 != null);
        this.kind = kind;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prefix prefix = (Prefix) o;
        return kind == prefix.kind && Objects.equals(data1, prefix.data1) && Objects.equals(data2, prefix.data2) && Objects.equals(data3, prefix.data3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, data1, data2, data3);
    }

    @Override
    public String prettyPrint(int depth) {
        return null;
    }

    public enum PrefixKind {
        PREFIX_KIND1, PREFIX_KIND2, PREFIX_KIND3
    }
}
