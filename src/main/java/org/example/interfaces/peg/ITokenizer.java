package org.example.interfaces.peg;

import org.example.models.peg.token.Token;

public interface ITokenizer {
    Token[] tokenize(String text);
}
