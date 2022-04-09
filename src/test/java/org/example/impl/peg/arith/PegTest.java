package org.example.impl.peg.arith;

import org.example.impl.jtree.JavaBackend;
import org.example.impl.peg.Generator;
import org.example.impl.peg.Peg;
import org.example.impl.peg.Tokenizer;
import org.example.impl.peg.TwoStepParser;
import org.example.util.CompilerWrapper;
import org.example.util.PathClassloader;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PegTest {

    @Test
    void compile() {
        try {
            var source = Path.of("src/test/resources/peg_cst.peg");
            source = Path.of("src/test/resources/org/example/impl/peg/arith/assets/arith.peg");

            var compilationDirectory = Path.of("src/test/resources/org/example/impl/peg/arith/assets/");

            String grammarText = Files.readString(source, StandardCharsets.US_ASCII);
            String testerClass = Files.readString(Path.of("src/test/resources/org/example/impl/peg/arith/assets/TestPlayGround.java"));

            String generatedCode = Peg.compile(grammarText, new Tokenizer(), new TwoStepParser(), new Generator(), new JavaBackend(JavaBackend.ADTEmulationKind.TAG_FIELD_AND_PLAIN_UNUNIONED_VARIANTS));
            System.out.println(generatedCode);

            try {
                CompilerWrapper.compile("Program", generatedCode, compilationDirectory);
                CompilerWrapper.compile("TestPlayground", testerClass, compilationDirectory);

                var pathClassLoader = new PathClassloader(this.getClass().getClassLoader(), Path.of("C:/Users/jitrs/IdeaProjects/peg-generate/src/test/resources/"));

                Class<?> tester = pathClassLoader.loadClass("org.example.impl.peg.arith.assets.TestPlayground");

                System.out.println(tester);

                Method testMethod = tester.getMethod("test", String.class);

                var input = "(2+3)*(60/2+7)"; /*185 is 5 * 37*/
                var expected = 185;

                assertEquals(expected, testMethod.invoke(null, input));
            } finally {
                CompilerWrapper.cleanup(compilationDirectory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}