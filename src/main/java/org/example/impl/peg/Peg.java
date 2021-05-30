package org.example.impl.peg;

import org.example.interfaces.jtree.IJtreeBackend;
import org.example.interfaces.peg.IGenerator;
import org.example.interfaces.peg.IParser;
import org.example.interfaces.peg.ITokenizer;

public class Peg {
    private Peg() {
    }

    public static String compile(String text, ITokenizer tokenizer, IParser parser, IGenerator generator, IJtreeBackend backend) {
        return backend.transpile(generator.generate(parser.parse(tokenizer.tokenize(text))));
    }
}
