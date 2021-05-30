package org.example.models.peg.cst;

import java.util.Arrays;
import java.util.Objects;

public final class Postfix extends CstNode {
    public final Prefix data1;
    public final PostfixC[] data2;

    public Postfix(Prefix data1, PostfixC[] data2) {
        Objects.requireNonNull(data1);
        Objects.requireNonNull(data2);
        this.data1 = data1;
        this.data2 = data2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Postfix postfix = (Postfix) o;
        return data1.equals(postfix.data1) && Arrays.equals(data2, postfix.data2);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(data1);
        result = 31 * result + Arrays.hashCode(data2);
        return result;
    }

    @Override
    public String prettyPrint(int depth) {
        return null;
    }
}
