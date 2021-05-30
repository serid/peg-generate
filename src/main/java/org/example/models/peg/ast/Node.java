package org.example.models.peg.ast;

import org.example.interfaces.util.IPrettyPrintable;
import org.example.util.Util;

import java.util.Arrays;
import java.util.Objects;

// Sum type for rule nodes ("kind + plain fields" emulation)
public final class Node implements IPrettyPrintable {
    public final NodeKind kind;

    // A union of (String) | (Node, Node)
    public final String data;
    public final Node node1;
    public final Node[] nodes;

    public Node(NodeKind kind, String data) {
        assert kind.is_nullary();
        assert data != null && !data.equals("");

        this.kind = kind;
        this.data = data;
        this.node1 = null;
        this.nodes = null;
    }

    public Node(NodeKind kind, Node node) {
        assert kind.is_unary();
        assert node != null;

        this.kind = kind;
        this.data = null;
        this.node1 = node;
        this.nodes = null;
    }

    public Node(NodeKind kind, Node[] nodes) {
        assert kind.is_binary();
        assert nodes != null;

        this.kind = kind;
        this.data = null;
        this.node1 = null;
        this.nodes = nodes;
    }

    @Override
    public String toString() {
//        return "Node{" +
//                "kind=" + kind +
//                ", data='" + data + '\'' +
//                ", node1=" + node1 +
//                ", nodes=" + Arrays.toString(nodes) +
//                '}';
        throw new RuntimeException();
    }

    @Override
    public String prettyPrint(int depth) {
        var tabs = Util.tabulate(depth);
        var tabs1 = Util.tabulate(depth + 1);
        return tabs + "node {" + "\n" +
                tabs1 + "kind = " + kind + "\n" +
                (data == null ? "" : tabs1 + "data = '" + data + '\'' + "\n") +
                (node1 == null ? "" : tabs1 + "node1 = \n" + node1.prettyPrint(depth + 1) + "\n") +
                (nodes == null ? "" : tabs1 + "nodes = " + "[\n" + Arrays.stream(nodes)
                        .map((node) -> node.prettyPrint(depth + 2) + ",\n")
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) +
                        tabs1 + "]" + "\n") +
                tabs + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return kind == node.kind && Objects.equals(data, node.data) && Objects.equals(node1, node.node1) && Arrays.equals(nodes, node.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, data, node1, Arrays.hashCode(nodes));
    }

    public enum NodeKind {
        ACOTERMINAL, NONTERMINAL, SEQUENCE, ORDERED_CHOICE, ZERO_OR_MORE, ONE_OR_MORE, OPTIONAL, AND_PREDICATE, NOT_PREDICATE;

        public boolean is_nullary() {
            return this == ACOTERMINAL || this == NONTERMINAL;
        }

        public boolean is_prefix() {
            return this == AND_PREDICATE || this == NOT_PREDICATE;
        }

        public boolean is_postfix() {
            return this == ZERO_OR_MORE || this == ONE_OR_MORE || this == OPTIONAL;
        }

        public boolean is_unary() {
            return is_prefix() || is_postfix();
        }

        public boolean is_binary() {
            return this == SEQUENCE || this == ORDERED_CHOICE;
        }
    }
}
