package org.example.models.peg.cst;

import java.util.Objects;

public final class CstGrammar extends CstNode {
    public final String packageName;
    public final String[] externs;
    public final CstRule[] data;

    public CstGrammar(String packageName, String[] externs, CstRule[] data) {
        Objects.requireNonNull(packageName);
        Objects.requireNonNull(externs);
        Objects.requireNonNull(data);
        this.packageName = packageName;
        this.externs = externs;
        this.data = data;
    }

    @Override
    public String prettyPrint(int depth) {
        return null;
    }
}
