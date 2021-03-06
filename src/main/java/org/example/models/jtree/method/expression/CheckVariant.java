package org.example.models.jtree.method.expression;

import org.example.models.jtree.type.datatype.Datatype;
import org.example.models.jtree.type.datatype.DatatypeVariable;

import java.util.Objects;

/**
 * Checks that an expression has certain type
 */
final public class CheckVariant extends Expression {
    public final Datatype containing_datatype; // type of "e"
    public final DatatypeVariable variant;
    public final Expression e;

    public CheckVariant(Datatype containing_datatype, DatatypeVariable variant, Expression e) {
        Objects.requireNonNull(containing_datatype);
        Objects.requireNonNull(variant);
        Objects.requireNonNull(e);
        this.containing_datatype = containing_datatype;
        this.variant = variant;
        this.e = e;
    }
}
