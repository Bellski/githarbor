package ru.githarbor.frontend.monaco.java;

import ru.githarbor.frontend.monaco.gsi.java.impl.JavaFile;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Singleton
public class JavaFileProvider {

    private final Map<String, JavaFile> metadataByAbsolutePath = new HashMap<>();

    @Inject
    public JavaFileProvider() {
    }

    public void putIfAbsent(String absolutePath, JavaFile javaFile) {
        metadataByAbsolutePath.putIfAbsent(absolutePath, javaFile);
    }

    public void computeIfAbsent(String key, Function<? super String, ? extends JavaFile> mappingFunction) {
        metadataByAbsolutePath.computeIfAbsent(key, mappingFunction);
    }

    public JavaFile getJavaFile(String absolutePath) {
        return metadataByAbsolutePath.get(absolutePath);
    }
}
