package org.example.models.jtree.type;

import java.util.Objects;

public final class ArrayTypeReference extends TypeReference {
    public final TypeReference inner;

    public ArrayTypeReference(TypeReference inner) {
        Objects.requireNonNull(inner);
        this.inner = inner;
    }
}
