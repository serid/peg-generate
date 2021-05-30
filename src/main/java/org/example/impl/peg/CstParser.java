package org.example.impl.peg;

import org.example.models.peg.cst.*;
import org.example.models.peg.token.Token;
import org.example.util.Unit;

import java.util.ArrayList;

public class CstParser {
    private Token[] tokens;
    private int return_i;

    public CstParser() {
    }

    private int skip_newlines(int i) {
        // skip newlines
        while (i < tokens.length && tokens[i].kind == Token.TokenKind.NEWLINE)
            i++;
        return i;
    }

    private Unit parse_token(int i, Token.TokenKind kind) {
        if (i < tokens.length && tokens[i].kind == kind) {
            return_i = i + 1;
            return Unit.instance;
        }
        return null;
    }

    private AcoTerminal parse_aco_terminal(int i) {
        if (i < tokens.length && tokens[i].kind == Token.TokenKind.ACOTERMINAL) {
            return_i = i + 1;
            return new AcoTerminal(tokens[i].data);
        }
        return null;
    }

    private NonTerminal parse_non_terminal(int i) {
        if (i < tokens.length && tokens[i].kind == Token.TokenKind.NONTERMINAL) {
            return_i = i + 1;
            return new NonTerminal(tokens[i].data);
        }
        return null;
    }

    private String parse_package(int i) {
        var unit1 = parse_token(i, Token.TokenKind.PACKAGE);
        i = return_i;
        if (unit1 != null) {
            var var1 = parse_non_terminal(i);
            i = return_i;
            if (var1 != null) {
                var unit2 = parse_token(i, Token.TokenKind.NEWLINE);
                i = return_i;
                if (unit2 != null) {
                    // token provider imports are prefixed with TPR:
                    return var1.data;
                }
            }
        }

        return null;
    }

    private String parse_extern(int i) {
        var unit1 = parse_token(i, Token.TokenKind.EXTERN);
        i = return_i;
        if (unit1 != null) {
            var var1 = parse_non_terminal(i);
            i = return_i;
            if (var1 != null) {
                var unit2 = parse_token(i, Token.TokenKind.AS_TOKENPROVIDER);
                i = return_i;
                boolean isTokenProvider = unit2 != null;

                var unit3 = parse_token(i, Token.TokenKind.NEWLINE);
                i = return_i;
                if (unit3 != null) {
                    // token provider imports are prefixed with TPR:
                    return (isTokenProvider ? "TPR:" : "") + var1.data;
                }
            }
        }

        return null;
    }

    private Value parse_value(int i) {
        var data1 = parse_aco_terminal(i);
        i = return_i;
        if (data1 != null) {
            return new Value(Value.ValueKind.VALUE_KIND1, data1, null, null);
        }

        var data2 = parse_non_terminal(i);
        i = return_i;
        if (data2 != null) {
            return new Value(Value.ValueKind.VALUE_KIND2, null, data2, null);
        }

        var _unit1 = parse_token(i, Token.TokenKind.PARENL);
        i = return_i;
        if (_unit1 != null) {
            var data3 = parse_ordered_choice(i);
            i = return_i;
            if (data3 != null) {
                var _unit2 = parse_token(i, Token.TokenKind.PARENR);
                i = return_i;
                if (_unit2 != null) {
                    return new Value(Value.ValueKind.VALUE_KIND3, null, null, data3);
                }
            }
        }
        return null;
    }

    private Prefix parse_prefix(int i) {
        var _unit1 = parse_token(i, Token.TokenKind.AMPERSAND);
        i = return_i;
        if (_unit1 != null) {
            var data1 = parse_prefix(i);
            i = return_i;
            if (data1 != null) {
                return new Prefix(Prefix.PrefixKind.PREFIX_KIND1, data1, null, null);
            }
        }

        var _unit2 = parse_token(i, Token.TokenKind.EXCLAMATION_MARK);
        i = return_i;
        if (_unit2 != null) {
            var data2 = parse_prefix(i);
            i = return_i;
            if (data2 != null) {
                return new Prefix(Prefix.PrefixKind.PREFIX_KIND2, null, data2, null);
            }
        }

        var data3 = parse_value(i);
        i = return_i;
        if (data3 != null) {
            return new Prefix(Prefix.PrefixKind.PREFIX_KIND3, null, null, data3);
        }
        return null;
    }

    private PostfixC parse_postfix1(int i) {
        var _unit1 = parse_token(i, Token.TokenKind.ASTERISK);
        i = return_i;
        if (_unit1 != null) {
            return new PostfixC(PostfixC.PostfixCKind.POSTFIX_C_KIND1);
        }

        var _unit2 = parse_token(i, Token.TokenKind.PLUS);
        i = return_i;
        if (_unit2 != null) {
            return new PostfixC(PostfixC.PostfixCKind.POSTFIX_C_KIND2);
        }

        var _unit3 = parse_token(i, Token.TokenKind.QUESTION_MARK);
        i = return_i;
        if (_unit3 != null) {
            return new PostfixC(PostfixC.PostfixCKind.POSTFIX_C_KIND3);
        }
        return null;
    }

    private Postfix parse_postfix(int i) {
        var data1 = parse_prefix(i);
        i = return_i;
        if (data1 != null) {
            var arr = new ArrayList<PostfixC>();
            while (true) {
                var datai = parse_postfix1(i);
                i = return_i;
                if (datai != null) {
                    arr.add(datai);
                    continue;
                }
                break;
            }
            return new Postfix(data1, arr.toArray(PostfixC[]::new));
        }
        return null;
    }

    private Sequence parse_sequence(int i) {
        var data1 = parse_postfix(i);
        i = return_i;
        if (data1 != null) {
            var arr = new ArrayList<Postfix>();
            while (true) {
                var datai = parse_postfix(i);
                i = return_i;
                if (datai != null) {
                    arr.add(datai);
                    continue;
                }
                break;
            }
            return new Sequence(data1, arr.toArray(Postfix[]::new));
        }
        return null;
    }

    private OrderedChoice parse_ordered_choice(int i) {
        var data1 = parse_sequence(i);
        i = return_i;
        if (data1 != null) {
            var arr = new ArrayList<Sequence>();
            while (true) {
                var _unit = parse_token(i, Token.TokenKind.SLASH);
                i = return_i;
                if (_unit != null) {
                    var datai = parse_sequence(i);
                    i = return_i;
                    if (datai != null) {
                        arr.add(datai);
                        continue;
                    }
                }
                break;
            }
            return new OrderedChoice(data1, arr.toArray(Sequence[]::new));
        }
        return null;
    }

    private CstRule parse_rule(int i) {
        var data1 = parse_non_terminal(i);
        i = return_i;
        if (data1 != null) {
            var _unit = parse_token(i, Token.TokenKind.LEFT_ARROW);
            i = return_i;
            if (_unit != null) {
                var data2 = parse_ordered_choice(i);
                i = return_i;
                if (data2 != null) {
                    var _unit2 = parse_token(i, Token.TokenKind.NEWLINE);
                    i = return_i;
                    if (_unit2 != null) {
                        return new CstRule(data1, data2);
                    }
                }
            }
        }
        return null;
    }

    private CstGrammar parse_grammar(int i) {
        var packageName = parse_package(i);
        i = return_i;

        if (packageName != null) {
            var externs = new ArrayList<String>();
            while (true) {
                i = skip_newlines(i);
                var externName = parse_extern(i);
                i = return_i;

                if (externName != null) {
                    externs.add(externName);
                    continue;
                }
                break;
            }

            var arr = new ArrayList<CstRule>();
            while (true) {
                i = skip_newlines(i);

                var rule = parse_rule(i);
                i = return_i;
                if (rule != null) {
                    arr.add(rule);
                    continue;
                }
                break;
            }

            return new CstGrammar(packageName, externs.toArray(String[]::new), arr.toArray(CstRule[]::new));
        }

        return null;
    }

    public CstGrammar parse(Token[] tokens) {
        this.tokens = tokens;
        return parse_grammar(0);
    }
}
