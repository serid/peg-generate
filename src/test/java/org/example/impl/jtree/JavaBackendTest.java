package org.example.impl.jtree;

import org.example.interfaces.jtree.IJtreeBackend;
import org.example.models.jtree.Program;
import org.example.models.jtree.method.Method;
import org.example.models.jtree.method.expression.CheckVariant;
import org.example.models.jtree.method.expression.GetField;
import org.example.models.jtree.method.expression.This;
import org.example.models.jtree.method.statement.IfThenElse;
import org.example.models.jtree.method.statement.Return;
import org.example.models.jtree.method.statement.Statement;
import org.example.models.jtree.type.DataTypeReference;
import org.example.models.jtree.type.datatype.Datatype;
import org.example.models.jtree.type.datatype.DatatypeVariable;
import org.example.util.Lazy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JavaBackendTest {

    @Test
    void transpile() {
        DatatypeVariable[] variants = new DatatypeVariable[]{
                new DatatypeVariable(new DataTypeReference(Datatype.getInt()), "a"),
                new DatatypeVariable(new DataTypeReference(new Datatype("Error")), "b")
        };

        Lazy<Method[]> methods = new Lazy<>();

        var example_jtree_program = new Program("a", new String[]{}, new Datatype[]{
                new Datatype("Result", true, variants, methods)
        });

        methods.resolve(new Method[]{
                new Method("unwrap", new DatatypeVariable[]{}, new Statement[]{
                        new IfThenElse(new CheckVariant(example_jtree_program.datatypes[0], variants[0], new This()),
                                new Return(new GetField(new This(), example_jtree_program.datatypes[0].variants[0].name)),
                                new Return(new GetField(new This(), example_jtree_program.datatypes[0].variants[1].name)))
                }, new DataTypeReference(Datatype.getUnit()))
        });

        IJtreeBackend back = new JavaBackend(JavaBackend.ADTEmulationKind.TAG_FIELD_AND_PLAIN_UNUNIONED_VARIANTS);

        var text = back.transpile(example_jtree_program);

        assertEquals("public class Result {\n" +
                "    public final ResultKind tag;\n" +
                "    public final int a;\n" +
                "    public final Error b;\n" +
                "\n" +
                "    public Result(int a, Error b) {\n" +
                "        ((this).a) = (a);\n" +
                "        ((this).b) = (b);\n" +
                "    }\n" +
                "\n" +
                "    public Unit unwrap() {\n" +
                "        if ((this).tag == Result.ResultKind.Ok) {\n" +
                "            return (this).a;\n" +
                "        } else {\n" +
                "            return (this).b;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public enum ResultKind { OK, ERR, };\n" +
                "}\n" +
                "\n", text);
    }
}