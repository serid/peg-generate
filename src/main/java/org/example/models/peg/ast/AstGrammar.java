package org.example.models.peg.ast;

import java.util.Arrays;
import java.util.Objects;

public final class AstGrammar {
    public final String packageName;
    public final String[] externs;
    public final AstRule[] rules;

    public AstGrammar(String packageName, String[] externs, AstRule[] rules) {
        Objects.requireNonNull(packageName);
        Objects.requireNonNull(externs);
        Objects.requireNonNull(rules);
        this.packageName = packageName;
        this.externs = externs;
        this.rules = rules;
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "rules=" + Arrays.toString(rules) +
                '}';
    }
}
