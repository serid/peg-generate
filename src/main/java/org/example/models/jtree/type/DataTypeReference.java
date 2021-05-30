package org.example.models.jtree.type;

import org.example.models.jtree.type.datatype.Datatype;

import java.util.Objects;

public final class DataTypeReference extends TypeReference {
    public final String name;

    public DataTypeReference(String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    public DataTypeReference(Datatype data_type) {
        Objects.requireNonNull(data_type);
        this.name = data_type.name;
    }
}
