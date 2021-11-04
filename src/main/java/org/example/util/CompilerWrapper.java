package org.example.util;

import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class CompilerWrapper {
    public static void compile(String name, String code, Path output) {
        System.out.println("CWD: " + getCurrentWorkingDirectory());

        var out = new StringWriter();

        var tool = ToolProvider.getSystemJavaCompiler();

        var task = tool.getTask(out, null, null,
                Arrays.asList("-classpath", "./src/test/resources/"), null,
                Collections.singletonList(new JavaSourceFromString(name, code)));

        var ok = task.call();

        if (!ok) {
            System.out.println(out.getBuffer());
            throw new RuntimeException("compilation failed");
        }

        // find generated classfiles (including inner classes)
        var classFiles = findClassfiles(Path.of(".")).toArray(Path[]::new);

        try {
            for (var path : classFiles) {
//                System.out.println(path + " to " + output.resolve(path.getFileName()));
                Files.move(path, output.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // remove all classfiles in directory
    public static void cleanup(Path where) {
        var classFiles = findClassfiles(where).toArray(Path[]::new);

        try {
            for (var path : classFiles) {
//                System.out.println(path + " to " + where.resolve(path.getFileName()));
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCurrentWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    // find generated classfiles (including inner classes)
    private static Stream<Path> findClassfiles(Path where) {
        try {
            return Files.find(where, 1, (path, _a) -> path.toString().endsWith(".class"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
