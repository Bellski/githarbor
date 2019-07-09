package ru.githarbor.frontend.monaco.gsi.java.impl;

import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.GsiIdentifier;
import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.frontend.monaco.gsi.java.GsiJavaClass;
import ru.githarbor.frontend.monaco.gsi.java.GsiJavaMethod;
import ru.githarbor.frontend.monaco.gsi.java.variable.GsiJavaVariable;
import ru.githarbor.shared.JavaElementType;

import java.util.stream.Stream;

public class GsiJavaClassImpl extends BaseGsiElement implements GsiJavaClass {

    private final boolean isStatic;

    public GsiJavaClassImpl(Range range, GsiElement parent, boolean isStatic, boolean isEnum) {
        super(range, JavaElementType.CLASS, parent);

        this.isStatic = isStatic;
    }

    @Override
    public String getText() {
        return getIdentifier().getText();
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public GsiElement findFieldByName(String name) {
        return Stream.of(findChildrenByType(JavaElementType.FIELD))
                .filter(field -> ((GsiJavaVariable) field).getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public GsiElement[] findMethodsByName(String name) {
        return Stream.of(findChildrenByType(JavaElementType.METHOD))
                .filter(method -> ((GsiJavaMethod) method).getName().equals(name))
                .toArray(GsiElement[]::new);
    }

    @Override
    public GsiElement findInnerClassByName(String name) {
        return Stream.of(findChildrenByType(JavaElementType.CLASS))
                .filter(innerClass -> ((GsiJavaClass) innerClass).getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getName() {
        return getIdentifier().getText();
    }

    @Override
    public GsiIdentifier getIdentifier() {
        return (GsiIdentifier) findChildByType(JavaElementType.NAME);
    }
}
