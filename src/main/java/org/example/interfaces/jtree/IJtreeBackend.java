package org.example.interfaces.jtree;

import org.example.models.jtree.Program;

/**
 * Interface for classes that can transpile Jtree to other programming languages
 */
public interface IJtreeBackend {
    String transpile(Program program);
}
