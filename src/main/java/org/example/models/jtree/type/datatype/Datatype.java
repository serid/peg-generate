package org.example.models.jtree.type.datatype;

import org.example.models.jtree.method.Method;
import org.example.util.Lazy;

import java.util.Objects;

/**
 * Class represents Algebraic Data Types that will be emitted to output code
 */
public final class Datatype {
    private static final Datatype object = new Datatype("Object");
    private static final Datatype unit = new Datatype("Unit");
    private static final Datatype int_ = new Datatype("int");
    public final String name;
    public final boolean is_builtin;
    public final boolean isSumtype;
    public final DatatypeVariable[] variants;
    public final Lazy<Method[]> methods;

    public Datatype(String name, boolean isSumtype, DatatypeVariable[] variants, Lazy<Method[]> methods) {
        Objects.requireNonNull(name);
        assert !name.equals("");
        Objects.requireNonNull(variants);
        Objects.requireNonNull(methods);

        this.name = name;
        this.isSumtype = isSumtype;
        this.is_builtin = false;
        this.variants = variants;
        this.methods = methods;
    }

    public Datatype(String name) {
        Objects.requireNonNull(name);
        assert !name.equals("");

        this.name = name;
        this.is_builtin = true;
        this.isSumtype = false;
        this.variants = new DatatypeVariable[]{};
        this.methods = new Lazy<>(new Method[]{});
    }

    public static Datatype getUnit() {
        return unit;
    }

    public static Datatype getInt() {
        return int_;
    }

    public static Datatype getObject() {
        return object;
    }
}
