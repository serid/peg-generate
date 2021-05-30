package org.example.models.peg.ast;

import java.util.Objects;

public final class AstRule {
    public final String name;
    public final Node node;

    public AstRule(String name, Node node) {
        if (name == null || node == null)
            throw new IllegalArgumentException();

        this.name = name;
        this.node = node;
    }

    @Override
    public String toString() {
        return "Rule {" +
                "name = '" + name + "', " +
                "node = \n" + node.prettyPrint(1) + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AstRule rule = (AstRule) o;
        return name.equals(rule.name) && node.equals(rule.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, node);
    }
}
