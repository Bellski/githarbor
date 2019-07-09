package ru.githarbor.shared;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import java.util.List;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class JavaSourceMetadataDTO {
    public int[] packag3;
    public String[] symbols;
    public SourceElementDTO[] elements;
    public int[][] imports;

    @JsOverlay
    public static JavaSourceMetadataDTO create(int[] packag3, List<String> symbols, List<SourceElementDTO> elements, List<int[]> imports) {
        final JavaSourceMetadataDTO javaSourceMetadata = new JavaSourceMetadataDTO();
        javaSourceMetadata.packag3 = packag3;
        javaSourceMetadata.symbols = symbols.toArray(new String[0]);
        javaSourceMetadata.elements = elements.toArray(new SourceElementDTO[0]);
        javaSourceMetadata.imports = imports.toArray(new int[0][0]);

        return javaSourceMetadata;
    }
}
