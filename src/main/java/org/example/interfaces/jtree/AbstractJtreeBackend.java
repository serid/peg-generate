package org.example.interfaces.jtree;

import org.example.util.Util;
import org.example.util.StringBuilderFormat;

/**
 * Provides utility methods for JTree backends
 */
public abstract class AbstractJtreeBackend implements IJtreeBackend {
    protected final StringBuilder sb = new StringBuilder();
    protected int indent;
    protected int lineCount = 1; // start at two because new_line won't be invoked until second line is reached

    /**
     * Call before transpiling the tree to insert initial line number
     */
    protected void init() {
        insertLineNumber();
    }

    /**
     * Use at line beginning to insert indentation
     */
    protected void place_indentation() {
        Util.tabulate(sb, indent);
    }

    protected void new_line() {
        sb.append("\n");
        insertLineNumber();
    }

    protected void insertLineNumber() {
        StringBuilderFormat.format(sb, "/* %s */ ", String.valueOf(lineCount++));
    }
}
