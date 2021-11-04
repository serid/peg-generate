package org.example.impl.jtree;

import org.example.interfaces.jtree.AbstractJtreeBackend;
import org.example.models.jtree.Program;
import org.example.models.jtree.method.Method;
import org.example.models.jtree.method.expression.*;
import org.example.models.jtree.method.statement.IfThenElse;
import org.example.models.jtree.method.statement.Return;
import org.example.models.jtree.method.statement.Statement;
import org.example.models.jtree.type.DataTypeReference;
import org.example.models.jtree.type.PointerToType;
import org.example.models.jtree.type.TypeReference;
import org.example.models.jtree.type.datatype.Datatype;
import org.example.models.jtree.type.datatype.DatatypeVariable;
import org.example.util.StringBuilderFormat;
import org.example.util.exception.NonexhaustiveMatchingError;

public class CBackend extends AbstractJtreeBackend {
    private void transpile_typereference(TypeReference typeReference) {
        if (typeReference instanceof DataTypeReference) {
            DataTypeReference dataTypeReference = (DataTypeReference) typeReference;
            sb.append(dataTypeReference.name);
        } else if (typeReference instanceof PointerToType) {
            PointerToType pointerToType = (PointerToType) typeReference;
            transpile_typereference(pointerToType.pointee);
            sb.append("*");
        } else {
            throw new NonexhaustiveMatchingError(typeReference);
        }
    }

    private void transpile_variable(DatatypeVariable datatypeVariable) {
        transpile_typereference(datatypeVariable.type);
        sb.append(" ").append(datatypeVariable.name);
    }

    private void transpile_parameters(String type_name, DatatypeVariable[] parameters) {
        StringBuilderFormat.format(sb, "(%s *this", type_name);

        // Transpile zero-th parameter then transpile others while separating them with ", "
        for (DatatypeVariable parameter : parameters) {
            sb.append(", ");
            transpile_variable(parameter);
        }
        sb.append(")");
    }

    private void transpile_expression(Expression expression) {
        if (expression instanceof CheckVariant) {
            CheckVariant checkVariant = (CheckVariant) expression;
            sb.append("(");
            transpile_expression(checkVariant.e);
            StringBuilderFormat.format(sb,
                    ").tag == %s",
                    checkVariant.variant.name.toUpperCase());
        } else if (expression instanceof This) {
            sb.append("this");
        } else if (expression instanceof GetField) {
            GetField getField = (GetField) expression;
            sb.append("(");
            transpile_expression(getField.e);
            sb.append(").").append(getField.fieldName);
        } else if (expression instanceof Deref) {
            Deref deref = (Deref) expression;
            sb.append("*(");
            transpile_expression(deref.e);
            sb.append(")");
        } else {
            throw new NonexhaustiveMatchingError(expression);
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
        } else if (statement instanceof IfThenElse) {
            IfThenElse ifThenElse = (IfThenElse) statement;

            place_indentation();
            sb.append("if (");
            transpile_expression(ifThenElse.condition);
            sb.append(") {");
            new_line();

            System.out.println("unimplemented");
//            indent++;
//            for (Statement statement2 : ifThenElse.on_true) {
//                transpile_statement(statement2);
//            }
//            indent--;

            place_indentation();
            sb.append("} else {");
            new_line();

            System.out.println("unimplemented");
//            indent++;
//            for (Statement statement2 : ifThenElse.on_false) {
//                transpile_statement(statement2);
//            }
//            indent--;

            place_indentation();
            sb.append("}");
            new_line();
        } else {
            throw new NonexhaustiveMatchingError(statement);
        }
    }

    private void transpile_method(String type_name, Method method) {
        place_indentation();
        // Append return type and method name
        transpile_typereference(method.return_type);
        sb.append(" ").append(method.name);

        // Append parameters
        transpile_parameters(type_name, method.parameters);
        sb.append(" {");
        new_line();

        indent++;
        // Append method body
        for (var statement : method.block.statements) {
            transpile_statement(statement);
        }
        indent--;

        place_indentation();
        sb.append("}");
        new_line();
    }

    private void transpile_datatype(Datatype dataType) {
        for (var variant : dataType.variants) {
            place_indentation();
            sb.append("typedef struct {");
            new_line();

            System.out.println("unimplemented");
//            indent++;
//            for (var field : variant) {
//                place_indentation();
//                transpile_variable(field);
//                sb.append(";");
//                new_line();
//            }
//            indent--;

            place_indentation();
            StringBuilderFormat.format(sb, "} %s_%s;", dataType.name, variant.name);
            new_line();
            new_line();
        }

        place_indentation();
        sb.append("typedef struct {");
        new_line();

        indent++;

        place_indentation();
        sb.append("enum { ");
        for (var data : dataType.variants) {
            sb.append(data.name.toUpperCase()).append(", ");
        }
        sb.append("} tag;");
        new_line();

        place_indentation();
        sb.append("union {");
        new_line();

        indent++;
        for (var variant : dataType.variants) {
            place_indentation();
            StringBuilderFormat.format(sb, "%s_%s %s;", dataType.name, variant.name, variant.name);
            new_line();
        }
        indent--;

        place_indentation();
        sb.append("};");
        new_line();

        indent--;

        place_indentation();
        StringBuilderFormat.format(sb, "} %s;", dataType.name);
        new_line();
        new_line();

        for (var method : dataType.methods.get()) {
            transpile_method(dataType.name, method);
        }
    }

    private void transpile_program(Program program) {
        for (var datatype : program.datatypes) {
            transpile_datatype(datatype);
        }
    }

    @Override
    public String transpile(Program program) {
        transpile_program(program);
        return sb.toString();
    }
}
