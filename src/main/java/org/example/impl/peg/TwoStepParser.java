package org.example.impl.peg;

import org.example.interfaces.peg.IParser;
import org.example.models.peg.ast.AstGrammar;
import org.example.models.peg.cst.CstGrammar;
import org.example.models.peg.token.Token;

public class TwoStepParser implements IParser {
    @Override
    public AstGrammar parse(Token[] tokens) {
        CstGrammar g = new CstParser().parse(tokens);
        return AstFromCst.convert(g);
    }
}
