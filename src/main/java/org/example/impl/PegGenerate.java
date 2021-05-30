package org.example.impl;

import org.example.impl.jtree.JavaBackend;
import org.example.impl.peg.Generator;
import org.example.impl.peg.Peg;
import org.example.impl.peg.Tokenizer;
import org.example.impl.peg.TwoStepParser;
import org.example.util.CompilerWrapper;

import java.nio.file.Path;

public class PegGenerate {
    private PegGenerate() {
    }

    public static void main(String[] args) {
        try {
//            test_compile();
//            check_compiler();
        } catch (RuntimeException e) {
            if (e.getCause() == null)
                e.printStackTrace();
            else
                e.getCause().printStackTrace();
        }
    }

    private static void test_compile() {
        System.out.println(Peg.compile("", new Tokenizer(), new TwoStepParser(), new Generator(), new JavaBackend(JavaBackend.ADTEmulationKind.TAG_FIELD_AND_PLAIN_UNUNIONED_VARIANTS)));
    }

    private static void check_compiler() {
        CompilerWrapper.compile("A", "class A {}", Path.of("src/A.class"));
    }
}
