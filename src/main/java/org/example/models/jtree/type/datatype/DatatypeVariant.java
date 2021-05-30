package org.example.models.jtree.type.datatype;

import java.util.Objects;

public final class DatatypeVariant {
    public final String name;
    public final DatatypeVariable[] fields;

    public DatatypeVariant(String name, DatatypeVariable[] fields) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(fields);
        this.name = name;
        this.fields = fields;
    }
}
