package org.example.impl.jtree;

import org.example.interfaces.jtree.AbstractJtreeBackend;
import org.example.models.jtree.Program;
import org.example.models.jtree.method.Method;
import org.example.models.jtree.method.expression.*;
import org.example.models.jtree.method.statement.*;
import org.example.models.jtree.type.ArrayTypeReference;
import org.example.models.jtree.type.DataTypeReference;
import org.example.models.jtree.type.PointerToType;
import org.example.models.jtree.type.TypeReference;
import org.example.models.jtree.type.datatype.Datatype;
import org.example.models.jtree.type.datatype.DatatypeVariable;
import org.example.util.Lazy;
import org.example.util.StringBuilderFormat;
import org.example.util.exception.NonexhaustiveMatchingError;

import java.util.Arrays;
import java.util.stream.Stream;

public class JavaBackend extends AbstractJtreeBackend {
    private final ADTEmulationKind adtEmulationKind;

    public JavaBackend(ADTEmulationKind adtEmulationKind) {
        assert adtEmulationKind == ADTEmulationKind.TAG_FIELD_AND_PLAIN_UNUNIONED_VARIANTS;
        this.adtEmulationKind = adtEmulationKind;
    }

    private void transpile_typereference(TypeReference typeReference) {
        if (typeReference instanceof DataTypeReference) {
            DataTypeReference dataTypeReference = (DataTypeReference) typeReference;
            sb.append(dataTypeReference.name);
        } else if (typeReference instanceof PointerToType) {
            throw new UnsupportedOperationException();
        } else if (typeReference instanceof ArrayTypeReference) {
            ArrayTypeReference arrayTypeReference = (ArrayTypeReference) typeReference;
            transpile_typereference(arrayTypeReference.inner);
            sb.append("[]");
        } else {
            throw new NonexhaustiveMatchingError(typeReference);
        }
    }

    private void transpile_variable(DatatypeVariable datatypeVariable) {
        transpile_typereference(datatypeVariable.type);
        sb.append(" ").append(datatypeVariable.name);
    }

    private void transpile_field(DatatypeVariable datatypeVariable) {
        place_indentation();
        sb.append("public "); // idk maybe add "final"
        transpile_variable(datatypeVariable);
        sb.append(";");
        new_line();
    }

    private void transpile_parameters(String typeName, DatatypeVariable[] parameters) {
        // Transpile zero-th parameter then transpile others while separating them with ", "

        sb.append("(");

        if (parameters.length > 0) {
            transpile_variable(parameters[0]);

            for (int i = 1; i < parameters.length; i++) {
                sb.append(", ");
                transpile_variable(parameters[i]);
            }
        }

        sb.append(")");
    }

    private void transpile_arguments(Expression[] arguments) {
        sb.append("(");

        if (arguments.length > 0) {
            transpile_expression(arguments[0]);

            for (int i = 1; i < arguments.length; i++) {
                sb.append(", ");
                transpile_expression(arguments[i]);
            }
        }

        sb.append(")");
    }

    private void transpile_expression_parenthesized(Expression expression) {
        sb.append("(");
        transpile_expression(expression);
        sb.append(")");
    }

    private void transpile_expression(Expression expression) {
        if (expression instanceof This) {
            sb.append("this");
        } else if (expression instanceof GetIdentValue) {
            GetIdentValue getIdentValue = (GetIdentValue) expression;
            sb.append(getIdentValue.name);
        } else if (expression instanceof CheckVariant) {
            CheckVariant checkVariant = (CheckVariant) expression;
            transpile_expression_parenthesized(checkVariant.e);
            StringBuilderFormat.format(sb,
                    ".tag == %s.%sKind.%s",
                    checkVariant.containing_datatype.name,
                    checkVariant.containing_datatype.name,
                    checkVariant.variant.name);
        } else if (expression instanceof GetField) {
            GetField getField = (GetField) expression;
            transpile_expression_parenthesized(getField.e);
            sb.append(".").append(getField.fieldName);
        } else if (expression instanceof MethodCall) {
            MethodCall methodCall = (MethodCall) expression;
            if (methodCall.receiver != null) {
                transpile_expression_parenthesized(methodCall.receiver);
                sb.append(".");
            }
            sb.append(methodCall.name);
            transpile_arguments(methodCall.arguments);
        } else if (expression instanceof BinaryOperator) {
            BinaryOperator binaryOperator = (BinaryOperator) expression;

            transpile_expression_parenthesized(binaryOperator.e1);
            sb.append(" ").append(binaryOperator.symbol).append(" ");
            transpile_expression_parenthesized(binaryOperator.e2);
        } else if (expression instanceof NewCall) {
            NewCall newCall = (NewCall) expression;
            StringBuilderFormat.format(sb, "new %s", newCall.typeName);
            transpile_arguments(newCall.arguments.get());
        } else if (expression instanceof BooleanConstant) {
            BooleanConstant booleanConstant = (BooleanConstant) expression;
            sb.append(booleanConstant.data);
        } else if (expression instanceof StringConstant) {
            StringConstant stringConstant = (StringConstant) expression;
            StringBuilderFormat.format(sb, "\"%s\"", stringConstant.data);
        } else if (expression instanceof Null) {
            sb.append("null");
        } else if (expression instanceof Deref) {
            throw new UnsupportedOperationException();
        } else {
            throw new NonexhaustiveMatchingError(expression);
        }
    }

    private void transpile_block(Block block, boolean omitBracesEh) {
        if (!omitBracesEh) {
            place_indentation();
//            sb.append("/*block*/");
            sb.append("{");
            new_line();
            indent++;
        }

        // omit braces when blocks are nested
        for (var statement2 : block.statements) {
            if (statement2 instanceof Block)
                transpile_block((Block) statement2, true);
            else
                transpile_statement(statement2);
        }

        if (!omitBracesEh) {
            indent--;
            place_indentation();
            sb.append("}");
            new_line();
        }
    }

    private void transpile_statement(Statement statement) {
        if (statement instanceof Return) {
            Return return_ = (Return) statement;

            place_indentation();
            sb.append("return ");
            transpile_expression(return_.value);
            sb.append(";");
            new_line();
        } else if (statement instanceof ExpressionStatement) {
            ExpressionStatement expressionStatement = (ExpressionStatement) statement;

            place_indentation();
            transpile_expression(expressionStatement.expression);
            sb.append(";");
            new_line();
        } else if (statement instanceof IfThenElse) {
            IfThenElse ifThenElse = (IfThenElse) statement;

            place_indentation();
            sb.append("if (");
            transpile_expression(ifThenElse.condition);
            sb.append(")");
            new_line();

            assert ifThenElse.on_true instanceof Block;
            transpile_statement(ifThenElse.on_true);

            assert ifThenElse.on_false instanceof Block;

            if (((Block) ifThenElse.on_false).statements.length > 0) {
                place_indentation();
                sb.append("else");
                new_line();

                transpile_statement(ifThenElse.on_false);
            }
        } else if (statement instanceof Block) {
            Block block = (Block) statement;
            transpile_block(block, false);
        } else if (statement instanceof While) {
            While aWhile = (While) statement;

            place_indentation();
            sb.append("while (");
            transpile_expression(aWhile.condition);
            sb.append(")");
            new_line();

            assert aWhile.body instanceof Block;
            transpile_statement(aWhile.body);
        } else if (statement instanceof Declaration) {
            Declaration declaration = (Declaration) statement;

            place_indentation();
            transpile_typereference(declaration.dataType);
            StringBuilderFormat.format(sb, " %s = ", declaration.name);
            transpile_expression(declaration.initializer);
            sb.append(";");
            new_line();
        } else if (statement instanceof Break) {
            place_indentation();
            sb.append("break;");
            new_line();
        } else if (statement instanceof Continue) {
            place_indentation();
            sb.append("continue;");
            new_line();
        } else {
            throw new NonexhaustiveMatchingError(statement);
        }
    }

    private void transpile_method(String typeName, Method method, boolean isConstructor) {
        place_indentation();
        sb.append("public ");
        if (method.staticEh) sb.append("static ");
        // Append return type and method name
        if (!isConstructor) {
            transpile_typereference(method.return_type);
            sb.append(" ");
        }
        sb.append(method.name);

        // Append parameters
        transpile_parameters(typeName, method.parameters);
        sb.append(" {");
        new_line();

        indent++;
        transpile_block(method.block, true);
        indent--;

        place_indentation();
        sb.append("}");
        new_line();
    }

    private void transpile_datatype(Datatype dataType) {
        place_indentation();
        StringBuilderFormat.format(sb, "public static final class %s {", dataType.name);
        new_line();

        indent++;

        DatatypeVariable[] plainFields = new DatatypeVariable[]{};

        // transpile variants and fields
        if (dataType.isSumtype) {
            // sum type

            var tag = Stream.of(
                    new DatatypeVariable(new DataTypeReference(dataType.name + "Kind"), "tag")
            );

            var fields = Arrays.stream(dataType.variants)
                    // ignore unit fields
                    .filter(field -> !(field.type instanceof DataTypeReference &&
                            ((DataTypeReference) field.type).name.equals("Unit")));

            // join tag and other fields
            plainFields = Stream.concat(tag, fields).toArray(DatatypeVariable[]::new);
        } else {
            // record type

            plainFields = dataType.variants;
        } /*else {
            throw new IllegalArgumentException("dataType.variants should be nonempty");
        }*/

        // place fields, one per line
        for (var field : plainFields) {
            transpile_field(field);
        }

        new_line();

        // transpile constructor
        transpile_method(dataType.name, new Method(dataType.name, plainFields,
                Arrays.stream(plainFields)
                        .map(field -> new ExpressionStatement(
                                new BinaryOperator("=", new GetField(new This(), field.name), new GetIdentValue(field.name))))
                        .toArray(Statement[]::new),
                new DataTypeReference("invalid")
        ), true);

        new_line();

        // if a sum type
        if (dataType.isSumtype) {
            // for each variant generate a "make" method
            for (int i = 0; i < dataType.variants.length; i++) {
                // filter out Unit fields
                var filtered_fields = Stream.of(dataType.variants[i])
                        .filter(field -> !(field.type instanceof DataTypeReference &&
                                ((DataTypeReference) field.type).name.equals("Unit")))
                        .toArray(DatatypeVariable[]::new);

                // first parameter is tag
                var tag = Stream.of(
                        (Expression) new GetIdentValue(dataType.name + "Kind." + "KIND_" + (i + 1))
                );

                // add "dataType.variants.length" arguments where only "i"-th is nonnull "filtered_fields[0].name"
                int finalI = i;
                DatatypeVariable[] finalPlainFields = plainFields;
                var arguments = Stream.iterate(0, n -> n < finalPlainFields.length - 1, n -> n + 1)
                        .map(n -> {
                            if (n == finalI)
                                return new GetIdentValue(filtered_fields[0].name);
                            else
                                return new Null();
                        });

                arguments = Stream.concat(tag, arguments);

                transpile_method(dataType.name, new Method("Make" + (i + 1), filtered_fields,
                        new Statement[]{
                                new Return(new NewCall(dataType.name, new Lazy<>(
                                        arguments.toArray(Expression[]::new)
                                )))
                        },
                        new DataTypeReference(dataType.name), true), false);

                new_line();
            }
        }

        // transpile methods
        for (var method : dataType.methods.get()) {
            transpile_method(dataType.name, method, false);
            new_line();
        }

        // transpile Kind enum
        if (dataType.isSumtype) {
            place_indentation();
            StringBuilderFormat.format(sb, "public enum %sKind { ", dataType.name);
            for (var data : dataType.variants) {
                sb.append(data.name.toUpperCase()).append(", ");
            }
            sb.append("}");
            new_line();
        } else {
            // remove linefeed lol
            sb.deleteCharAt(sb.length() - 1);
        }

        indent--;

        place_indentation();
        sb.append("}");
        new_line();
        new_line();
    }

    private void transpile_program(Program program) {
        StringBuilderFormat.format(sb, "package %s;", program.packageName);
        new_line();

        for (var importName : program.imports) {
            StringBuilderFormat.format(sb, "import %s;", importName);
            new_line();
        }

        new_line();

        place_indentation();
        sb.append("public final class Program {");
        new_line();
        indent++;

        for (var datatype : program.datatypes) {
            transpile_datatype(datatype);
        }

        indent--;
        place_indentation();
        sb.append("}");
        new_line();
    }

    @Override
    public String transpile(Program program) {
        init();
        transpile_program(program);
        return sb.toString();
    }

    public enum ADTEmulationKind {
        // TAG_FIELD_AND_PLAIN_UNUNIONED_VARIANTS example
        // class Result<T, E> {
        //     ResultKind tag;
        //     T ok;
        //     E err;
        //
        //     public static class ResultKind { OK, ERR, }
        // }
        TAG_FIELD_AND_PLAIN_UNUNIONED_VARIANTS,
        OTHER,
    }
}
