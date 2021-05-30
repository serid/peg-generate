package org.example.impl.jtree;

import org.example.interfaces.jtree.IJtreeBackend;
import org.example.models.jtree.Program;
import org.example.models.jtree.method.Method;
import org.example.models.jtree.method.expression.CheckVariant;
import org.example.models.jtree.method.expression.Deref;
import org.example.models.jtree.method.expression.GetField;
import org.example.models.jtree.method.expression.This;
import org.example.models.jtree.method.statement.IfThenElse;
import org.example.models.jtree.method.statement.Return;
import org.example.models.jtree.method.statement.Statement;
import org.example.models.jtree.type.DataTypeReference;
import org.example.models.jtree.type.datatype.Datatype;
import org.example.models.jtree.type.datatype.DatatypeVariable;
import org.example.models.jtree.type.datatype.DatatypeVariant;
import org.example.util.Lazy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CBackendTest {

    @Test
    void transpile() {
        var variants = new DatatypeVariant[]{
                new DatatypeVariant("Ok",
                        new DatatypeVariable[]{
                                new DatatypeVariable(new DataTypeReference(Datatype.getInt()), "a")}
                ),
                new DatatypeVariant("Err",
                        new DatatypeVariable[]{
                                new DatatypeVariable(new DataTypeReference(new Datatype("Error")), "b")}
                )
        };

        Lazy<Method[]> methods = new Lazy<>();

        var example_jtree_program = new Program("a", new String[]{}, new Datatype[]{
                new Datatype("Result", variants, methods)
        });

        methods.resolve(new Method[]{
                new Method("unwrap", new DatatypeVariable[]{}, new Statement[]{
                        new IfThenElse(new CheckVariant(example_jtree_program.datatypes[0], variants[0], new Deref(new This())),
                                new Return(new GetField(new Deref(new This()), example_jtree_program.datatypes[0].variants[0].fields[0].name)),
                                new Return(new GetField(new Deref(new This()), example_jtree_program.datatypes[0].variants[1].fields[0].name)))
                }, new DataTypeReference(Datatype.getUnit()))
        });

        IJtreeBackend back = new CBackend();

        var text = back.transpile(example_jtree_program);

        assertEquals("typedef struct {\n" +
                "    int a;\n" +
                "} Result_Ok;\n" +
                "\n" +
                "typedef struct {\n" +
                "    Error b;\n" +
                "} Result_Err;\n" +
                "\n" +
                "typedef struct {\n" +
                "    enum { OK, ERR, } tag;\n" +
                "    union {\n" +
                "        Result_Ok Ok;\n" +
                "        Result_Err Err;\n" +
                "    };\n" +
                "} Result;\n" +
                "\n" +
                "Unit unwrap(Result *this) {\n" +
                "    if ((*(this)).tag == OK) {\n" +
                "        return (*(this)).data;\n" +
                "    } else {\n" +
                "        return (*(this)).err;\n" +
                "    }\n" +
                "}\n", text);
    }
}