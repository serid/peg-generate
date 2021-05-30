package org.example.models.peg.cst;

import java.util.Arrays;
import java.util.Objects;

public final class OrderedChoice extends CstNode {
    public final Sequence data1;
    public final Sequence[] data2;

    public OrderedChoice(Sequence data1, Sequence[] data2) {
        Objects.requireNonNull(data1);
        Objects.requireNonNull(data2);
        this.data1 = data1;
        this.data2 = data2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedChoice that = (OrderedChoice) o;
        return data1.equals(that.data1) && Arrays.equals(data2, that.data2);
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
