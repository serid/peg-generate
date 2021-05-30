package org.example.models.jtree;

import org.example.models.jtree.type.datatype.Datatype;

import java.util.Objects;

public final class Program {
    public final String packageName;
    public final String[] imports;
    public final Datatype[] datatypes;

    public Program(String packageName, String[] imports, Datatype[] datatypes) {
        Objects.requireNonNull(packageName);
        Objects.requireNonNull(imports);
        Objects.requireNonNull(datatypes);
        this.packageName = packageName;
        this.imports = imports;
        this.datatypes = datatypes;
    }
}
