package org.example.impl.peg.arith;

import org.example.impl.jtree.JavaBackend;
import org.example.impl.peg.Generator;
import org.example.impl.peg.Peg;
import org.example.impl.peg.Tokenizer;
import org.example.impl.peg.TwoStepParser;
import org.example.util.CompilerWrapper;
import org.example.util.PathClassloader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PegTest {

    @Test
    void compile() {
        var source = Path.of("src/test/resources/peg_cst.peg");
        source = Path.of("src/test/resources/org/example/impl/peg/arith/assets/arith.peg");

        String content;
        try {
            content = Files.readString(source, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        var generatedCode = Peg.compile(content, new Tokenizer(), new TwoStepParser(), new Generator(), new JavaBackend(JavaBackend.ADTEmulationKind.TAG_FIELD_AND_PLAIN_UNUNIONED_VARIANTS));

//        System.out.println(generatedCode);

        var compilationDirectory = Path.of("src/test/resources/org/example/impl/peg/arith/assets/");

        String testerClass;
        try {
            testerClass = Files.readString(Path.of("src/test/resources/org/example/impl/peg/arith/assets/TestPlayGround.java"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CompilerWrapper.compile("Program", generatedCode, compilationDirectory);
        CompilerWrapper.compile("TestPlayground", testerClass, compilationDirectory);

        try {
            var pathClassLoader = new PathClassloader(this.getClass().getClassLoader(), Path.of("C:/Users/jitrs/IdeaProjects/peg-generate/src/test/resources/"));

            Class<?> tester = pathClassLoader.loadClass("org.example.impl.peg.arith.assets.TestPlayground");

            System.out.println(tester);

            Method testMethod = tester.getMethod("test", String.class);

            var input = "(2+3)*(60/2+7)"; /*185 is 5 * 37*/
            var expected = 185;

            // assertEquals(185, TestPlayground.test(input));
            try {
                assertEquals(185, testMethod.invoke(null, input));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            CompilerWrapper.cleanup(compilationDirectory);
        }
    }
}