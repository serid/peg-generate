package org.example.interfaces.util;

public interface IPrettyPrintable {
    String prettyPrint(int depth);

    default String prettyPrint() {
        return prettyPrint(0);
    }
}
