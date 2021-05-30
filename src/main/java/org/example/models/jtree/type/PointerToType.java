package org.example.models.jtree.type;

import java.util.Objects;

/**
 * Class representing syntactic references to pointers to types in code
 */
public final class PointerToType extends TypeReference {
    public final TypeReference pointee;

    public PointerToType(TypeReference pointee) {
        Objects.requireNonNull(pointee);
        this.pointee = pointee;
    }
}
