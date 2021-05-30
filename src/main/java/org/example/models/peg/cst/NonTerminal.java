package org.example.models.peg.cst;

import java.util.Objects;

public final class NonTerminal extends CstNode {
    public final String data;

    public NonTerminal(String data) {
        assert data != null && !data.equals("");
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NonTerminal that = (NonTerminal) o;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String prettyPrint(int depth) {
        return null;
    }
}
