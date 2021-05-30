package org.example.models.jtree.method;

import org.example.models.jtree.method.statement.Block;
import org.example.models.jtree.method.statement.Statement;
import org.example.models.jtree.type.TypeReference;
import org.example.models.jtree.type.datatype.DatatypeVariable;

import java.util.Objects;

public final class Method {
    public final String name;
    public final DatatypeVariable[] parameters;
    public final Block block;
    public final TypeReference return_type;
    public final boolean staticEh;

    public Method(String name, DatatypeVariable[] parameters, Statement[] statements, TypeReference return_type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(statements);
        Objects.requireNonNull(return_type);
        this.name = name;
        this.parameters = parameters;
        this.block = new Block(statements);
        this.return_type = return_type;
        this.staticEh = false;
    }

    public Method(String name, DatatypeVariable[] parameters, Statement[] statements, TypeReference return_type, boolean staticEh) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(statements);
        Objects.requireNonNull(return_type);
        this.name = name;
        this.parameters = parameters;
        this.block = new Block(statements);
        this.return_type = return_type;
        this.staticEh = staticEh;
    }
}
