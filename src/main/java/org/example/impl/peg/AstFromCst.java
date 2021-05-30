package org.example.impl.peg;

import org.example.models.peg.ast.AstGrammar;
import org.example.models.peg.ast.AstRule;
import org.example.models.peg.ast.Node;
import org.example.models.peg.cst.*;
import org.example.util.exception.NonexhaustiveMatchingError;

import java.util.ArrayList;

public final class AstFromCst {
    private AstFromCst() {
    }

    private static Node convert_value(Value value) {
        if (value.kind == Value.ValueKind.VALUE_KIND1)
            return new Node(Node.NodeKind.ACOTERMINAL, value.data1.data);
        else if (value.kind == Value.ValueKind.VALUE_KIND2)
            return new Node(Node.NodeKind.NONTERMINAL, value.data2.data);
        else if (value.kind == Value.ValueKind.VALUE_KIND3)
            return convert_ordered_choice(value.data3);
        else throw new NonexhaustiveMatchingError(value);
    }

    private static Node convert_prefix(Prefix prefix) {
        if (prefix.kind == Prefix.PrefixKind.PREFIX_KIND1)
            return new Node(Node.NodeKind.AND_PREDICATE, convert_prefix(prefix.data1));
        else if (prefix.kind == Prefix.PrefixKind.PREFIX_KIND2)
            return new Node(Node.NodeKind.NOT_PREDICATE, convert_prefix(prefix.data2));
        else if (prefix.kind == Prefix.PrefixKind.PREFIX_KIND3)
            return convert_value(prefix.data3);
        else throw new NonexhaustiveMatchingError(prefix);
    }

    private static Node convert_postfix(Postfix postfix) {
        var node = convert_prefix(postfix.data1);
        for (var a : postfix.data2) {
            if (a.kind == PostfixC.PostfixCKind.POSTFIX_C_KIND1)
                node = new Node(Node.NodeKind.ZERO_OR_MORE, node);
            else if (a.kind == PostfixC.PostfixCKind.POSTFIX_C_KIND2)
                node = new Node(Node.NodeKind.ONE_OR_MORE, node);
            else if (a.kind == PostfixC.PostfixCKind.POSTFIX_C_KIND3)
                node = new Node(Node.NodeKind.OPTIONAL, node);
            else throw new NonexhaustiveMatchingError(a);
        }
        return node;
    }

    private static Node convert_sequence(Sequence sequence) {
        var node = convert_postfix(sequence.data1);

        // squash a 1-operand node to plain node
        if (sequence.data2.length == 0)
            return node;

        ArrayList<Node> list = new ArrayList<>();
        list.add(node);

        for (var a : sequence.data2) {
            list.add(convert_postfix(a));
        }
        return new Node(Node.NodeKind.SEQUENCE, list.toArray(Node[]::new));
    }

    private static Node convert_ordered_choice(OrderedChoice ordered_choice) {
        var node = convert_sequence(ordered_choice.data1);

        // squash a 1-operand node to plain node
        if (ordered_choice.data2.length == 0)
            return node;

        ArrayList<Node> list = new ArrayList<>();
        list.add(node);

        for (var a : ordered_choice.data2) {
            list.add(convert_sequence(a));
        }
        return new Node(Node.NodeKind.ORDERED_CHOICE, list.toArray(Node[]::new));
    }

    private static AstRule convert_rule(CstRule rule) {
        return new AstRule(rule.data1.data, convert_ordered_choice(rule.data2));
    }

    public static AstGrammar convert(CstGrammar g) {
        ArrayList<AstRule> list = new ArrayList<>();

        for (var a : g.data) {
            list.add(convert_rule(a));
        }

        return new AstGrammar(g.packageName, g.externs, list.toArray(AstRule[]::new));
    }
}
