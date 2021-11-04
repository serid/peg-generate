package org.example.impl.peg;

import org.example.interfaces.peg.IGenerator;
import org.example.models.jtree.Program;
import org.example.models.jtree.method.Method;
import org.example.models.jtree.method.expression.*;
import org.example.models.jtree.method.statement.*;
import org.example.models.jtree.type.ArrayTypeReference;
import org.example.models.jtree.type.DataTypeReference;
import org.example.models.jtree.type.datatype.Datatype;
import org.example.models.jtree.type.datatype.DatatypeVariable;
import org.example.models.peg.ast.AstGrammar;
import org.example.models.peg.ast.AstRule;
import org.example.models.peg.ast.Node;
import org.example.util.IntBox;
import org.example.util.Lazy;
import org.example.util.Pair;
import org.example.util.Util;
import org.example.util.exception.NonexhaustiveMatchingError;
import org.example.util.exception.UnreachableReachedError;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class Generator implements IGenerator {
    private static String externToTypeName(String s) {
        return s.substring(s.lastIndexOf('.') + 1);
    }

    private static boolean isTokenProviderExtern(String s) {
        return s.startsWith("TPR:");
    }

    private static String tokenProviderExternToUsualExtern(String s) {
        assert isTokenProviderExtern(s);
        return s.substring(4);
    }

    private Pair<Datatype, Method> rule_to_datatype_and_parsing_method(AstRule rule, Set<String> externs) {
        // remove acoterminals from rule tree to then derive datatype for the rule
        var deflated = Deflator.deflate(rule.node);

        // get datatype that will store parsed information
        var datatype = GetDatatype.get_datatype(rule.name, deflated, new Lazy<>(new Method[]{}));

        var parsing_method = new Method("parse" + rule.name,
                new DatatypeVariable[]{
                        new DatatypeVariable(new DataTypeReference(Datatype.getInt()), "i")
                },
                new Statement[]{
                        new Emitter(externs).get_node_parsing_method(rule)
                },
                new DataTypeReference(datatype)
        );

        return new Pair<>(datatype, parsing_method);
    }

    @Override
    public Program generate(AstGrammar grammar) {
        // # Process externs
        // set of type names declared extern
        var externs = new HashSet<String>();
        Stream.Builder<String> userImports = Stream.builder();

        String tokenProviderTypename = null;

        for (String externPath : grammar.externs) {
            var externName = externToTypeName(externPath);
            if (isTokenProviderExtern(externPath)) {
                tokenProviderTypename = externName;
                userImports.add(tokenProviderExternToUsualExtern(externPath));
            } else {
                externs.add(externName);
                userImports.add(externPath);
            }
        }

        if (tokenProviderTypename == null)
            throw new PegException("Could not locate TokenProvider type.\n" +
                    "Use \"extern a.b.c.T as token provider\" to declare a token provider.");


        // # Process grammar rules
        var datatypes = new ArrayList<Datatype>();
        var methods = new ArrayList<Method>();

        for (var r : grammar.rules) {
            var pair = rule_to_datatype_and_parsing_method(r, externs);
            datatypes.add(pair.a);
            methods.add(pair.b);
        }

        methods.add(new Method("main", new DatatypeVariable[]{}, new Statement[]{new Return(new Null())}, new DataTypeReference(Datatype.getUnit())));
        datatypes.add(new Datatype("Parser", false, new DatatypeVariable[]{
                // object that maps strings to tokens
                new DatatypeVariable(new DataTypeReference(tokenProviderTypename), "tokenProvider"),
                // Java does not support efficient tuples so parsing methods will return new i value in field return_i
                new DatatypeVariable(new DataTypeReference(Datatype.getInt()), "return_i"),
        }, new Lazy<>(methods.toArray(Method[]::new))));

        var systemImports = Stream.of(
                "org.example.impl.peg.arith.assets.Unit",
                "java.util.ArrayList"
        );

        var imports = Stream.concat(systemImports, userImports.build()).toArray(String[]::new);

        return new Program(grammar.packageName, imports, datatypes.toArray(Datatype[]::new));
    }

    private static final class Emitter {
        private final Set<String> externs;
        private int unit_var_index = 1;
        private int field_var_index = 1;

        public Emitter(Set<String> externs) {
            this.externs = Collections.unmodifiableSet(externs);
        }

        private String next_unit_var() {
            return "unit_" + unit_var_index++;
        }

        private String next_var() {
            return "var_" + field_var_index++;
        }

        private Statement node_visitor(String ruleName, Node node, int level, ExpressionClosure closure, ReturnGenerator generator) {
            if (node.kind == Node.NodeKind.ACOTERMINAL) {
                ArrayList<Statement> statements = new ArrayList<>();

                var var_name = next_unit_var();

                statements.add(new Declaration(
                        new DataTypeReference(Datatype.getObject()),
                        var_name,
                        new MethodCall(new GetIdentValue("tokenProvider"), "parse_token", new Expression[]{
                                new GetIdentValue("i"),
                                new StringConstant(node.data)
                        })));

                statements.add(new ExpressionStatement(new BinaryOperator("=",
                        new GetIdentValue("i"),
                        new BinaryOperator("=",
                                new GetIdentValue("return_i"),
                                new MethodCall(new GetIdentValue("tokenProvider"), "return_i", new Expression[]{})
                        )
                )));

                statements.add(new IfThenElse(new BinaryOperator("!=", new GetIdentValue(var_name), new Null()),
                        new Block(closure.apply(new Expression[]{}, generator)),
                        Block.empty));

                return new Block(statements.toArray(Statement[]::new));
            } else if (node.kind == Node.NodeKind.NONTERMINAL) {
                // call parser for rule "node.data"
                var caleeName = node.data;

                // check if calee is external
                var externalRuleEh = externs.contains(caleeName);

                // extern rules are called from "tokenProvider"
                var nameToCall = "parse" + node.data;

                // allocate variable for storing parsing result
                var var_name = next_var();

                ArrayList<Statement> statements = new ArrayList<>();

                if (externalRuleEh) {
                    statements.add(new Declaration(
                            new DataTypeReference(node.data),
                            var_name,
                            new MethodCall(new GetIdentValue("tokenProvider"), nameToCall, new Expression[]{
                                    new GetIdentValue("i"),
                            })));

                    statements.add(new ExpressionStatement(new BinaryOperator("=",
                            new GetIdentValue("i"),
                            new BinaryOperator("=",
                                    new GetIdentValue("return_i"),
                                    new MethodCall(new GetIdentValue("tokenProvider"), "return_i", new Expression[]{})
                            )
                    )));
                } else {
                    statements.add(new Declaration(
                            new DataTypeReference(node.data),
                            var_name,
                            new MethodCall(nameToCall, new Expression[]{
                                    new GetIdentValue("i"),
                            })));

                    statements.add(new ExpressionStatement(new BinaryOperator("=", new GetIdentValue("i"), new GetIdentValue("return_i"))));
                }

                statements.add(new IfThenElse(new BinaryOperator("!=", new GetIdentValue(var_name), new Null()),
                        new Block(new Statement[]{
                                closure.apply(new Expression[]{new GetIdentValue(var_name)}, generator)
                        }), Block.empty));

                return new Block(statements.toArray(Statement[]::new));
            } else if (node.kind.is_binary()) {
                assert node.nodes != null;

                if (node.kind == Node.NodeKind.SEQUENCE) {
                    // level2(nodes[0], (d1) ->
                    // level2(nodes[1], (d2) ->
                    // level2(nodes[2], (d3) ->
                    // ... level2(nodes[n-1], (d4) -> closure.apply(new String[]{d1, d2, d3, ..., dn})...)
                    // ✨

                    ExpressionClosure iter = closure;

                    for (int i = node.nodes.length - 1; i >= 1; i--) {
                        int finalI = i;
                        ExpressionClosure finalIter = iter;
                        iter = (components1, _generator1) -> node_visitor(ruleName, node.nodes[finalI], level + 1,
                                (components2, generator2) -> finalIter.apply(Util.concat(components1, components2), generator2),
                                generator
                        );
                    }

                    return node_visitor(ruleName, node.nodes[0], level + 1, iter, generator);
                } else if (node.kind == Node.NodeKind.ORDERED_CHOICE) {
                    // level2(nodes[0], closure)
                    // level2(nodes[1], closure)
                    // level2(nodes[2], closure)

                    var statements = new ArrayList<Statement>();

                    for (int i = 0; i < node.nodes.length; i++) {
                        // Add code to make proper sum type variant.
                        // This Return Generator replaces the default one (return new T(...);)
                        int finalI = i;
                        ReturnGenerator rg = (components) -> new Return(new MethodCall(ruleName + ".Make" + (finalI + 1), components));

                        statements.add(node_visitor(ruleName, node.nodes[i], level + 1,
                                closure, rg));
                    }

                    return new Block(statements.toArray(Statement[]::new));
                } else {
                    throw new NonexhaustiveMatchingError(node.kind);
                }
            } else if (node.kind.is_postfix()) {
                if (node.kind == Node.NodeKind.ZERO_OR_MORE) {
                    assert node.node1 != null;

                    var deflated_inner_node = Deflator.deflate(node.node1);

                    assert deflated_inner_node.data != null;

                    ArrayList<Statement> statements = new ArrayList<>();

                    var list_var = next_var();

                    var type_name = String.format("ArrayList<%s>", deflated_inner_node.data);

                    statements.add(new Declaration(
                            new DataTypeReference(type_name),
                            list_var,
                            new NewCall(type_name, new Lazy<>(new Expression[]{}))
                    ));

                    statements.add(new While(new BooleanConstant(true), new Block(new Statement[]{
                            node_visitor(ruleName, node.node1, level + 1, (_var_name, _generator1) -> new Block(new Statement[]{
                                    new ExpressionStatement(new MethodCall(new GetIdentValue(list_var), "add", new Expression[]{
                                            new GetIdentValue("var_" + (field_var_index - 1))
                                    })),
                                    new Continue()
                            }), generator),
                            new Break()
                    })));

                    statements.add(closure.apply(new Expression[]{
                            new MethodCall(new GetIdentValue(list_var), "toArray", new Expression[]{
                                    new GetIdentValue(String.format("%s[]::new", deflated_inner_node.data))
                            })
                    }, generator));

                    return new Block(statements.toArray(Statement[]::new));
                } else {
                    throw new NonexhaustiveMatchingError(node.kind);
                }
            } else if (node.kind.is_prefix()) {
                throw new UnreachableReachedError();
            } else {
                throw new NonexhaustiveMatchingError(node.kind);
            }
        }

        public Statement get_node_parsing_method(AstRule rule) {
            ReturnGenerator defaultRg = (components) -> new Return(new NewCall(rule.name, new Lazy<>(components)));
            ExpressionClosure c = (components, generator) -> generator.apply(components);

            // Add "return null;" after function code
            var rnull = new Return(new Null());

            var statement = node_visitor(rule.name, rule.node, 0, c, defaultRg);

            Statement[] new_statements;

            if (statement instanceof Block) {
                new_statements = Util.append(((Block) statement).statements, rnull);
            } else {
                new_statements = new Statement[]{
                        statement,
                        rnull
                };
            }

            return new Block(new_statements);
        }

        // type alias
        private interface ReturnGenerator extends Function<Expression[], Statement> {
        }

        // type alias
        private interface ExpressionClosure extends BiFunction<Expression[], Function<Expression[], Statement>, Statement> {
        }
    }

    private static final class Deflator {
        public static final Node unitNode = new Node(Node.NodeKind.NONTERMINAL, "Unit");

        private Deflator() {
        }

        /**
         * <p>
         * Convert an AST node like
         * "'&' Prefix / '!' Prefix / Value / 'a'"
         * to
         * "Prefix / Prefix / Value / Unit"
         * </p>
         * <p>
         * Process of deflation replaces {@link Node.NodeKind#ACOTERMINAL} with a special "Unit" nonterminal and
         * compacts sequences like ('a' A) and (A 'a') into just A but leaves choices like ('a' / A) intact.
         * </p>
         *
         * @param node Node to deflate
         * @return deflated node
         */
        private static Node deflate(Node node) {
            if (node.kind == Node.NodeKind.ACOTERMINAL)
                // acoterminals are discarded
                return unitNode;
            else if (node.kind == Node.NodeKind.NONTERMINAL)
                return node;
            else if (node.kind.is_postfix())
                // postfix operators pass deflation algorithm through
                return new Node(node.kind, deflate(node.node1));
            else if (node.kind.is_prefix())
                // prefix operators are discarded
                return unitNode;
            else if (node.kind == Node.NodeKind.SEQUENCE) {
                // sequences filter out unit nodes
                var nodes = Arrays.stream(node.nodes)
                        .map(Deflator::deflate)
                        .filter(n -> n != unitNode)
                        .toArray(Node[]::new);

                // if all subtrees were units
                if (nodes.length == 0)
                    return unitNode;

                // as usual, squash a 1-array to plain node
                if (nodes.length == 1)
                    return nodes[0];

                return new Node(Node.NodeKind.SEQUENCE, nodes);
            } else if (node.kind == Node.NodeKind.ORDERED_CHOICE) {
                // ordered_choice passes deflation algorithm through
                var nodes = Arrays.stream(node.nodes)
                        .map(Deflator::deflate)
                        .toArray(Node[]::new);
                return new Node(Node.NodeKind.ORDERED_CHOICE, nodes);
            } else {
                throw new NonexhaustiveMatchingError(node.kind);
            }
        }
    }

    private static final class GetDatatype {
        private GetDatatype() {
        }

        private static String getFieldName(boolean isInsideSumtype, int i) {
            return (isInsideSumtype ? "kind_" : "field_") + i;
        }

        private static DatatypeVariable level2(Node node, boolean isInsideSumtype, int i) {
            if (node.kind == Node.NodeKind.ACOTERMINAL) {
                // unreachable
                throw new UnreachableReachedError("all ACOTERMINALs should have been removed by 'deflate'");
            } else if (node.kind == Node.NodeKind.NONTERMINAL) {
                return new DatatypeVariable(new DataTypeReference(node.data), getFieldName(isInsideSumtype, i));
            } else if (node.kind.is_binary()) {
                throw new PegException("nested binary nodes are not allowed");
            } else if (node.kind.is_postfix()) {
                assert node.node1 != null;
                if (!node.node1.kind.is_nullary())
                    throw new PegException("nodes inside postfix operators have to be nullary");

                if (node.kind == Node.NodeKind.ZERO_OR_MORE || node.kind == Node.NodeKind.ONE_OR_MORE) {
                    return new DatatypeVariable(new ArrayTypeReference(new DataTypeReference(node.node1.data)), getFieldName(isInsideSumtype, i));
                } else if (node.kind == Node.NodeKind.OPTIONAL) {
                    return new DatatypeVariable(new DataTypeReference(node.node1.data), getFieldName(isInsideSumtype, i));
                } else {
                    throw new UnreachableReachedError();
                }
            } else if (node.kind.is_prefix()) {
                // prefix operators don't define datatype shape
                throw new UnreachableReachedError("all prefix operators should have been removed by 'deflate'");
            } else {
                throw new NonexhaustiveMatchingError(node.kind);
            }
        }

        /**
         * After syntax matched by {@param node} is parsed, its values need to be stored in AST tree.
         * This function derives fields needed to store node values after the string matched by node is parsed.
         * It also asserts that there are no binary nodes below top level. (This function is usually applied to deflated AST trees)
         *
         * @param node Node to infer fields from
         * @see Deflator#deflate(Node)
         */
        private static Pair<DatatypeVariable[], Boolean> level1(Node node) {
            if (node.kind.is_binary()) {
                assert node.nodes != null;

                // for every node, a variable will be computed
                var s = Arrays.stream(node.nodes);

                if (node.kind == Node.NodeKind.SEQUENCE) {
                    // return one variant with many variables

                    // each variable has to have a unique name
                    // TODO: rewrite to use "zip"
                    IntBox j = new IntBox(1);

                    // compute variables
                    // IntBox is not atomic, so no parallelism

                    return new Pair<>(s.sequential()
                            .map(inner -> level2(inner, false, j.getAndIncrement()))
                            .toArray(DatatypeVariable[]::new), false);
                } else if (node.kind == Node.NodeKind.ORDERED_CHOICE) {
                    // return many variants with one variable each

                    // each variant and variable has to have a unique name
                    // TODO: rewrite to use "zip"
                    IntBox i = new IntBox(1);

                    // IntBox is not atomic, so no parallelism
                    return new Pair<>(s.sequential()
                            .map(inner -> level2(inner, true, i.getAndIncrement()))
//                            .map(variable -> new DatatypeVariant("Kind" + i.getAndIncrement(), new DatatypeVariable[]{variable}))
                            .toArray(DatatypeVariable[]::new), true);
                } else {
                    throw new NonexhaustiveMatchingError(node.kind);
                }
            } else if (node.kind.is_nullary() || node.kind.is_unary()) {
                return new Pair<>(new DatatypeVariable[]{
                        level2(node, false, 1)
                }, false);
            } else {
                throw new NonexhaustiveMatchingError(node.kind);
            }
        }

        public static Datatype get_datatype(String rule_name, Node node, Lazy<Method[]> methods) {
            var pair = level1(node);
            return new Datatype(rule_name, pair.b, pair.a, methods);
        }
    }
}
