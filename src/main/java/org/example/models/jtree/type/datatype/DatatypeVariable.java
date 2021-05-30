package org.example.models.jtree.type.datatype;

import org.example.models.jtree.type.TypeReference;

import java.util.Objects;

public final class DatatypeVariable {
    public final TypeReference type;
    public final String name;

    public DatatypeVariable(TypeReference type, String name) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(name);
        this.type = type;
        this.name = name;
    }
}
