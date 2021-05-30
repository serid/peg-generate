package org.example.interfaces.peg;

import org.example.models.peg.ast.AstGrammar;
import org.example.models.peg.token.Token;

public interface IParser {
    AstGrammar parse(Token[] tokens);
}
