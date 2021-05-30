package org.example.interfaces.peg;

import org.example.models.jtree.Program;
import org.example.models.peg.ast.AstGrammar;

public interface IGenerator {
    Program generate(AstGrammar grammar);
}
