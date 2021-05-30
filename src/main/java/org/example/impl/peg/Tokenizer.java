package org.example.impl.peg;

import org.example.interfaces.peg.ITokenizer;
import org.example.models.peg.token.Token;
import org.example.util.Util;
import org.example.util.exception.NonexhaustiveMatchingError;
import org.example.util.exception.UnreachableReachedError;

import java.util.ArrayList;

public class Tokenizer implements ITokenizer {
    private static boolean string_starts_with_from_offset(String string, int offset, String prefix) {
        if (string.length() < prefix.length())
            return false;

        for (int i = 0; i < prefix.length(); i++) {
            if (string.charAt(offset + i) != prefix.charAt(i))
                return false;
        }

        return true;
    }

    private static boolean is_valid_terminal_character(char c) {
        return c != '\'';
    }

    private static boolean is_valid_nonterminal_character(char c) {
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '.');
    }

    private int skip_newline(String text, int i) {
        if (text.charAt(i) == '\n') {
            if (i + 1 < text.length()) {
                if (text.charAt(i + 1) == '\n') {
                    // linux/MacOS10 line ending
                    // skip one char, continue processing
                    i++;
                } else if (text.charAt(i + 1) == '\r') {
                    // invalid combination
                    throw new UnreachableReachedError();
                } else
                    i++;
            } else
                i++;
        } else if (text.charAt(i) == '\r') {
            if (i + 1 < text.length()) {
                if (text.charAt(i + 1) == '\n') {
                    // windows line ending
                    // skip two chars, continue processing
                    i++;
                    i++;
                } else if (text.charAt(i + 1) == '\r') {
                    // MacOS9 line ending
                    // skip one chars, continue processing
                    i++;
                } else
                    i++;
            } else
                i++;
        } else {
            // invalid combination
            throw new UnreachableReachedError();
        }
        return i;
    }

    @Override
    public Token[] tokenize(String text) {
        ArrayList<Token> tokens = new ArrayList<>();

        for (int i = 0; i < text.length(); ) {
            if (text.charAt(i) == ' ') {
                i++;
            } else if (Util.isLineFeed(text.charAt(i))) {
                tokens.add(new Token(Token.TokenKind.NEWLINE));
                i = skip_newline(text, i);
            } else if (text.charAt(i) == '(') {
                tokens.add(new Token(Token.TokenKind.PARENL));
                i++;
            } else if (text.charAt(i) == ')') {
                tokens.add(new Token(Token.TokenKind.PARENR));
                i++;
            } else if (text.charAt(i) == '/') {
                tokens.add(new Token(Token.TokenKind.SLASH));
                i++;
            } else if (text.charAt(i) == '*') {
                tokens.add(new Token(Token.TokenKind.ASTERISK));
                i++;
            } else if (text.charAt(i) == '+') {
                tokens.add(new Token(Token.TokenKind.PLUS));
                i++;
            } else if (text.charAt(i) == '?') {
                tokens.add(new Token(Token.TokenKind.QUESTION_MARK));
                i++;
            } else if (text.charAt(i) == '!') {
                tokens.add(new Token(Token.TokenKind.EXCLAMATION_MARK));
                i++;
            } else if (text.charAt(i) == '&') {
                tokens.add(new Token(Token.TokenKind.AMPERSAND));
                i++;
            } else if (string_starts_with_from_offset(text, i, "<-")) {
                tokens.add(new Token(Token.TokenKind.LEFT_ARROW));
                i += "<-".length();
            } else if (string_starts_with_from_offset(text, i, "package")) {
                tokens.add(new Token(Token.TokenKind.PACKAGE));
                i += "package".length();
            } else if (string_starts_with_from_offset(text, i, "extern")) {
                tokens.add(new Token(Token.TokenKind.EXTERN));
                i += "extern".length();
            } else if (string_starts_with_from_offset(text, i, "as token provider")) {
                tokens.add(new Token(Token.TokenKind.AS_TOKENPROVIDER));
                i += "as token provider".length();
            } else if (text.charAt(i) == '#') {
                // skip line comments
                i++;
                while (i < text.length() && !Util.isLineFeed(text.charAt(i)))
                    i++;
                // skip line feed
                if (i < text.length())
                    i = skip_newline(text, i);
            } else if (text.charAt(i) == '\'') {
                i++;

                var sb = new StringBuilder();

                while (is_valid_terminal_character(text.charAt(i))) {
                    sb.append(text.charAt(i));
                    i++;

                    if (i >= text.length()) {
                        throw new IllegalArgumentException("missing a closing quote");
                    }
                }

                if (text.charAt(i) == '\'') {
                    i++;
                } else {
                    throw new IllegalArgumentException("oops");
                }

                tokens.add(new Token(Token.TokenKind.ACOTERMINAL, sb.toString()));
            } else if (is_valid_nonterminal_character(text.charAt(i))) {
                var sb = new StringBuilder();

                while (is_valid_nonterminal_character(text.charAt(i))) {
                    sb.append(text.charAt(i));
                    i++;
                }

                tokens.add(new Token(Token.TokenKind.NONTERMINAL, sb.toString()));
            } else {
                throw new NonexhaustiveMatchingError(text.charAt(i));
            }
        }

        return tokens.toArray(Token[]::new);
    }
}
