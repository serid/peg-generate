package org.example.impl.peg;

import org.example.interfaces.peg.IParser;
import org.example.models.peg.ast.AstGrammar;
import org.example.models.peg.ast.AstRule;
import org.example.models.peg.ast.Node;
import org.example.models.peg.token.Token;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {
    // PEG grammar for PEG grammar
    private static final TestPair test1 = new TestPair(
            "Grammar <- Rule*\n" +
                    "Rule <- NonTerminal '<-' Expr '\\n'\n" +
                    "Expr <- OrderedChoice\n" +
                    "OrderedChoice <- Sequence ('/' Sequence)*\n" +
                    "Sequence <- SomeOrMore (SomeOrMore)*\n" +
                    "SomeOrMore <- Prefix ('+' / '*' / '?')*\n" +
                    "Prefix <- ('&' Prefix) / ('!' Prefix) / Value\n" +
                    "Value <- AcoTerminal / NonTerminal / '(' Expr ')'\n" +
                    "",
            new AstGrammar("a", new String[]{}, new AstRule[]{
                    new AstRule("Grammar", new Node(Node.NodeKind.ZERO_OR_MORE,
                            new Node(Node.NodeKind.NONTERMINAL, "Rule"))),
                    new AstRule("Rule", new Node(Node.NodeKind.SEQUENCE,
                            new Node[]{
                                    new Node(Node.NodeKind.NONTERMINAL, "NonTerminal"),
                                    new Node(Node.NodeKind.ACOTERMINAL, "<-"),
                                    new Node(Node.NodeKind.NONTERMINAL, "Expr"),
                                    new Node(Node.NodeKind.ACOTERMINAL, "\\n"),
                            })),
                    new AstRule("Expr", new Node(Node.NodeKind.NONTERMINAL, "OrderedChoice")),
                    new AstRule("OrderedChoice", new Node(Node.NodeKind.SEQUENCE,
                            new Node[]{
                                    new Node(Node.NodeKind.NONTERMINAL, "Sequence"),
                                    new Node(Node.NodeKind.ZERO_OR_MORE,
                                            new Node(Node.NodeKind.SEQUENCE,
                                                    new Node[]{
                                                            new Node(Node.NodeKind.ACOTERMINAL, "/"),
                                                            new Node(Node.NodeKind.NONTERMINAL, "Sequence"),
                                                    }))
                            })),
                    new AstRule("Sequence", new Node(Node.NodeKind.SEQUENCE,
                            new Node[]{

                                    new Node(Node.NodeKind.NONTERMINAL, "SomeOrMore"),
                                    new Node(Node.NodeKind.ZERO_OR_MORE,
                                            new Node(Node.NodeKind.NONTERMINAL, "SomeOrMore")),
                            })),
                    new AstRule("SomeOrMore", new Node(Node.NodeKind.SEQUENCE,
                            new Node[]{
                                    new Node(Node.NodeKind.NONTERMINAL, "Prefix"),
                                    new Node(Node.NodeKind.ZERO_OR_MORE,
                                            new Node(Node.NodeKind.ORDERED_CHOICE,
                                                    new Node[]{
                                                            new Node(Node.NodeKind.ACOTERMINAL, "+"),
                                                            new Node(Node.NodeKind.ACOTERMINAL, "*"),
                                                            new Node(Node.NodeKind.ACOTERMINAL, "?"),
                                                    })),
                            })),
                    new AstRule("Prefix", new Node(Node.NodeKind.ORDERED_CHOICE,
                            new Node[]{
                                    new Node(Node.NodeKind.SEQUENCE,
                                            new Node[]{
                                                    new Node(Node.NodeKind.ACOTERMINAL, "&"),
                                                    new Node(Node.NodeKind.NONTERMINAL, "Prefix"),
                                            }),
                                    new Node(Node.NodeKind.SEQUENCE,
                                            new Node[]{
                                                    new Node(Node.NodeKind.ACOTERMINAL, "!"),
                                                    new Node(Node.NodeKind.NONTERMINAL, "Prefix"),
                                            }),
                                    new Node(Node.NodeKind.NONTERMINAL, "Value")
                            })),
                    new AstRule("Value", new Node(Node.NodeKind.ORDERED_CHOICE,
                            new Node[]{
                                    new Node(Node.NodeKind.NONTERMINAL, "AcoTerminal"),
                                    new Node(Node.NodeKind.NONTERMINAL, "NonTerminal"),
                                    new Node(Node.NodeKind.SEQUENCE,
                                            new Node[]{
                                                    new Node(Node.NodeKind.ACOTERMINAL, "("),
                                                    new Node(Node.NodeKind.NONTERMINAL, "Expr"),
                                                    new Node(Node.NodeKind.ACOTERMINAL, ")")
                                            })
                            })),
            }));
    // Grammar for arithmetic expressions
    private static final TestPair test2 = new TestPair(
            "Expr <- Sum\n" +
                    "Sum <- Product (('+' / '-') Product)*\n" +
                    "Product <- Power (('*' / '/') Power)*\n" +
                    "Power <- Value ('^' Power)?\n" +
                    "Value <- Number / '(' Expr ')'\n" +
                    "",
            new AstGrammar("a", new String[]{}, new AstRule[]{
                    new AstRule("Expr", new Node(Node.NodeKind.NONTERMINAL, "Sum")),
                    new AstRule("Sum", new Node(Node.NodeKind.SEQUENCE,
                            new Node[]{
                                    new Node(Node.NodeKind.NONTERMINAL, "Product"),
                                    new Node(Node.NodeKind.ZERO_OR_MORE,
                                            new Node(Node.NodeKind.SEQUENCE,
                                                    new Node[]{
                                                            new Node(Node.NodeKind.ORDERED_CHOICE,
                                                                    new Node[]{
                                                                            new Node(Node.NodeKind.ACOTERMINAL, "+"),
                                                                            new Node(Node.NodeKind.ACOTERMINAL, "-"),
                                                                    }),
                                                            new Node(Node.NodeKind.NONTERMINAL, "Product")
                                                    }))
                            })),
                    new AstRule("Product", new Node(Node.NodeKind.SEQUENCE,
                            new Node[]{
                                    new Node(Node.NodeKind.NONTERMINAL, "Power"),
                                    new Node(Node.NodeKind.ZERO_OR_MORE,
                                            new Node(Node.NodeKind.SEQUENCE,
                                                    new Node[]{
                                                            new Node(Node.NodeKind.ORDERED_CHOICE,
                                                                    new Node[]{
                                                                            new Node(Node.NodeKind.ACOTERMINAL, "*"),
                                                                            new Node(Node.NodeKind.ACOTERMINAL, "/"),
                                                                    }),
                                                            new Node(Node.NodeKind.NONTERMINAL, "Power")
                                                    }))
                            })),
                    new AstRule("Power", new Node(Node.NodeKind.SEQUENCE,
                            new Node[]{
                                    new Node(Node.NodeKind.NONTERMINAL, "Value"),
                                    new Node(Node.NodeKind.OPTIONAL,
                                            new Node(Node.NodeKind.SEQUENCE,
                                                    new Node[]{
                                                            new Node(Node.NodeKind.ACOTERMINAL, "^"),
                                                            new Node(Node.NodeKind.NONTERMINAL, "Power")
                                                    }))
                            })),
                    new AstRule("Value", new Node(Node.NodeKind.ORDERED_CHOICE,
                            new Node[]{
                                    new Node(Node.NodeKind.NONTERMINAL, "Number"),
                                    new Node(Node.NodeKind.SEQUENCE,
                                            new Node[]{
                                                    new Node(Node.NodeKind.ACOTERMINAL, "("),
                                                    new Node(Node.NodeKind.NONTERMINAL, "Expr"),
                                                    new Node(Node.NodeKind.ACOTERMINAL, ")")
                                            })
                            })),
            }));

    private static <P extends IParser> void test_parser(P parser, TestPair test_pair) {
        Token[] tokens = new Tokenizer().tokenize(test_pair.input);
        AstGrammar g = parser.parse(tokens);

        assertEquals(test_pair.expected_output, g);
    }

    @Test
    void parse1() {
        test_parser(new TwoStepParser(), test1);
    }

    @Test
    void parse2() {
        test_parser(new TwoStepParser(), test2);
    }

    private static final class TestPair {
        public final String input;
        public final AstGrammar expected_output;

        public TestPair(String input, AstGrammar output) {
            this.input = input;
            this.expected_output = output;
        }
    }
}