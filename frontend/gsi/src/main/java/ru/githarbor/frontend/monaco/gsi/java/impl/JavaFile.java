package ru.githarbor.frontend.monaco.gsi.java.impl;

import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.frontend.monaco.gsi.java.GsiJavaClass;
import ru.githarbor.frontend.monaco.gsi.java.expression.GsiJavaReferenceExpression;
import ru.githarbor.frontend.monaco.gsi.java.statement.GsiJavaDeclarationStatement;
import ru.githarbor.frontend.monaco.gsi.java.variable.GsiJavaVariable;
import ru.githarbor.shared.JavaElementType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class JavaFile {

    private final String packag3;
    private final GsiElement[] elements;
    private final String[] imports;

    public JavaFile(String packag3, GsiElement[] elements, String[] imports) {
        this.packag3 = packag3;
        this.elements = elements;
        this.imports = imports;
    }

    public String getPackage() {
        return packag3;
    }

    public GsiElement[] resolveDepth(double line, double column) {

        final List<GsiElement> depth = new ArrayList<>();

        GsiElement inRange = Stream
                .of(elements)
                .filter(element -> element.getRange().inRange(line, column))
                .findFirst()
                .map(element -> {
                    if (isContext(element)) {
                        depth.add(element);
                    }

                    return element;
                })
                .orElse(null);

        while (inRange != null && inRange.getChildren() != null) {
            inRange = Stream
                    .of(inRange.getChildren())
                    .filter(element -> element.getRange().inRange(line, column))
                    .findFirst()
                    .map(element -> {
                        if (isContext(element)) {

                            if (element.getType() == JavaElementType.OBJECT_CREATION_EXPRESSION) {
                                if (element.findChildByType(JavaElementType.ANONYMOUS_CLASS_BODY_STATEMENT) != null) {
                                    depth.add(element);
                                }
                            } else {
                                depth.add(element);
                            }
                        }

                        return element;
                    })
                    .orElse(null);
        }

        return depth.toArray(new GsiElement[0]);
    }

    public Optional<GsiElement> findElementStartFrom(double line, double column) {
        return Stream.of(elements)
                .map(element -> element.findElementStartFrom(line, column))
                .filter(Objects::nonNull)
                .findFirst();
    }

    public GsiElement[] findUsages(GsiElement element) {
        GsiElement[] usages = new GsiElement[0];

        if (element instanceof GsiJavaVariable) {
            GsiElement parent = element.getParent();

            if (parent instanceof GsiJavaDeclarationStatement) {
                parent = parent.getParent();
            }

            usages = flatten(parent)
                    .filter(element1 ->
                            ((element1.getType() == JavaElementType.NAME) && (element1.getParent().getType() == JavaElementType.NAME_EXPRESSION))
                                    && element1.getText().equals(element.getText())
                    )
                    .filter(element1 -> {
                        final GsiJavaReferenceExpression nameExpression = (GsiJavaReferenceExpression) element1.getParent();
                        final GsiElement[] resolveResult = nameExpression.resolve();

                        return resolveResult.length == 1;
                    })
                    .map(GsiElement::getParent)
                    .toArray(GsiElement[]::new);
        } else if (element instanceof GsiJavaClass) {
            final GsiJavaClass rootClass = (GsiJavaClass) elements[0];

            usages = flatten(rootClass)
                    .filter(element1 ->
                            ((element1.getType() == JavaElementType.NAME) && (element1.getParent().getType() == JavaElementType.TYPE_EXPRESSION))
                                    && element1.getText().equals(element.getText()))
                    .filter(element1 -> {
                        final GsiJavaReferenceExpression nameExpression = (GsiJavaReferenceExpression) element1.getParent();
                        final GsiElement[] resolveResult = nameExpression.resolve();

                        return resolveResult.length == 1;
                    })
                    .map(GsiElement::getParent)
                    .toArray(GsiElement[]::new);
        }

        return usages;
    }

    public String findImportByPostfix(String postfix) {
        return Stream
                .of(imports)
                .filter(imp0rt -> imp0rt.endsWith(postfix))
                .findFirst()
                .orElse(null);
    }

    public Range getMainClassNameRange() {
        return elements[0].findChildByType(JavaElementType.NAME).getRange();
    }

    private static Stream<GsiElement> flatten(GsiElement element) {
        if (element.getChildren() == null) {
            return Stream.of(element);
        } else {
            return Stream.concat(Stream.of(element), Stream.of(element.getChildren()).flatMap(JavaFile::flatten));
        }
    }

    private static boolean isContext(GsiElement element) {
        return element.getType() == JavaElementType.CLASS ||
                element.getType() == JavaElementType.ENUM ||
                element.getType() == JavaElementType.FIELD ||
                element.getType() == JavaElementType.METHOD ||
                element.getType() == JavaElementType.LAMBDA_STATEMENT ||
                element.getType() == JavaElementType.OBJECT_CREATION_EXPRESSION;
    }
}
