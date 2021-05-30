package org.example.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class PathClassloader extends ClassLoader {
    public final Path classPath;

    public PathClassloader(ClassLoader parent, Path classPath) {
        super(parent);
        Objects.requireNonNull(classPath);
        this.classPath = classPath;
    }

    @Override
    protected Class<?> findClass(String className) {
        String filePath = className.replaceAll("\\.", "/").concat(".class");

        byte[] content;

        try {
            content = Files.readAllBytes(classPath.resolve(Path.of(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return defineClass(className, content, 0, content.length);
    }
}