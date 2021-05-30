package org.example.models.peg.token;

import java.util.Objects;

// Sum type for tokens ("kind + data" emulation)
public final class Token {
    public final TokenKind kind;
    public final String data;

    public Token(TokenKind kind, String data) {
        if (kind != TokenKind.ACOTERMINAL && kind != TokenKind.NONTERMINAL)
            throw new IllegalArgumentException(String.format("token of kind %s does not expect any token data", kind));

        Objects.requireNonNull(data);

        this.kind = kind;
        this.data = data;
    }

    public Token(TokenKind kind) {
        this.kind = kind;
        this.data = null;
    }

    public static void print_tokens(Token[] tokens) {
        for (var tok : tokens) {
            System.out.println(tok);
        }
    }

    @Override
    public String toString() {
        return data == null ? "Token{" +
                "kind=" + kind +
                '}' : "Token{" +
                "kind=" + kind +
                ", data=" + data +
                '}';
    }

    public enum TokenKind {
        PARENL, PARENR, SLASH, ASTERISK, PLUS, QUESTION_MARK, EXCLAMATION_MARK, AMPERSAND, LEFT_ARROW, ACOTERMINAL, NONTERMINAL,
        PACKAGE, EXTERN, AS_TOKENPROVIDER,
        NEWLINE
    }
}
