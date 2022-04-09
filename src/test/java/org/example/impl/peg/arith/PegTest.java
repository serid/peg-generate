package org.example.impl.peg.arith;

import org.example.impl.jtree.JavaBackend;
import org.example.impl.peg.Generator;
import org.example.impl.peg.Peg;
import org.example.impl.peg.Tokenizer;
import org.example.impl.peg.TwoStepParser;
import org.example.util.CompilerWrapper;
import org.example.util.PathClassloader;
import org.example.util.Util;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PegTest {

    Object compileAndRun(String grammar, String inputText) {
        try {
            var compilationDirectory = Path.of("./src/test/resources/org/example/impl/peg/arith/assets/");
            var classpath = "./src/test/resources/";

            String testerClass = Files.readString(Path.of("./src/test/resources/org/example/impl/peg/arith/assets/TestPlayGround.java"));

            String generatedCode = Peg.compile(grammar, new Tokenizer(), new TwoStepParser(), new Generator(), new JavaBackend(JavaBackend.ADTEmulationKind.TAG_FIELD_AND_PLAIN_UNUNIONED_VARIANTS));
            System.out.println(generatedCode);

            try {
                CompilerWrapper.compile("Program", generatedCode, classpath, compilationDirectory);
                CompilerWrapper.compile("TestPlayground", testerClass, classpath, compilationDirectory);

                var pathClassLoader = new PathClassloader(this.getClass().getClassLoader(), Path.of(classpath));

                Class<?> tester = pathClassLoader.loadClass("org.example.impl.peg.arith.assets.TestPlayground");

                System.out.println(tester);

                Method testMethod = tester.getMethod("test", String.class);

                return testMethod.invoke(null, inputText);
            } finally {
                CompilerWrapper.cleanup(compilationDirectory);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // NOTE: these tests can not be run in parallel due to reusing the same FS directory

    @Test
    void test1() {
        assertEquals(185, compileAndRun(Util.readString("src/test/resources/org/example/impl/peg/arith/assets/arith.peg"),
                "(2+3)*(60/2+7)"));
    }

    @Test
    void failTest() {
        assertNotEquals(200, compileAndRun(Util.readString("src/test/resources/org/example/impl/peg/arith/assets/arith.peg"),
                "(2+3)*(60/2+7)"));
    }
}