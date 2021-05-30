package org.example.impl.peg.arith.assets;

import java.util.ArrayList;
import java.util.HashMap;

public final class TokenProvider {
    private final Token[] tokens;
    private int return_i;

    private TokenProvider(Token[] tokens) {
        this.tokens = tokens;
        return_i = -1;
    }

    public static TokenProvider from(String text) {
        var tokens = new ArrayList<Token>();

        for (int i = 0; i < text.length(); ) {
            if (text.charAt(i) == '(') {
                tokens.add(new Token(Token.TokenKind.PARENL));
                i++;
            } else if (text.charAt(i) == ')') {
                tokens.add(new Token(Token.TokenKind.PARENR));
                i++;
            } else if (text.charAt(i) == '*') {
                tokens.add(new Token(Token.TokenKind.ASTERISK));
                i++;
            } else if (text.charAt(i) == '/') {
                tokens.add(new Token(Token.TokenKind.SLASH));
                i++;
            } else if (text.charAt(i) == '+') {
                tokens.add(new Token(Token.TokenKind.PLUS));
                i++;
            } else if (Character.isDigit(text.charAt(i))) {
                int n = 0;
                while (i < text.length() && Character.isDigit(text.charAt(i))) {
                    n *= 10;
                    n += text.charAt(i) - '0';
                    i++;
                }
                tokens.add(new Token(Token.TokenKind.INT, n));
            } else {
                throw new RuntimeException("nonexhaustive matching: " + text.charAt(i));
            }
        }

        return new TokenProvider(tokens.toArray(Token[]::new));
    }

    private static final HashMap<String, Token.TokenKind> tokenMap = new HashMap<>();

    static {
        tokenMap.put("+", Token.TokenKind.PLUS);
        tokenMap.put("-", Token.TokenKind.MINUS);
        tokenMap.put("*", Token.TokenKind.ASTERISK);
        tokenMap.put("/", Token.TokenKind.SLASH);
        tokenMap.put("(", Token.TokenKind.PARENL);
        tokenMap.put(")", Token.TokenKind.PARENR);
    }

    public Object parse_token(int i, String s) {
        if (i < tokens.length && tokens[i].kind == tokenMap.get(s)) {
            return_i = i + 1;
            return new Object();
        }
        return_i = i;
        return null;
    }

    public PegInt parsePegInt(int i) {
        if (i < tokens.length && tokens[i].kind == Token.TokenKind.INT) {
            return_i = i + 1;
            return new PegInt(tokens[i].data);
        }
        return_i = i;
        return null;
    }

    public int return_i() {
        return return_i;
    }

    private static final class Token {
        public final TokenKind kind;
        public final int data;

        public Token(TokenKind kind, int data) {
            assert kind == TokenKind.INT;
            this.kind = kind;
            this.data = data;
        }

        public Token(TokenKind kind) {
            assert kind != TokenKind.INT;
            this.kind = kind;
            this.data = 0;
        }

        public enum TokenKind {
            PLUS, MINUS, ASTERISK, SLASH, PARENL, PARENR, INT
        }
    }
}
