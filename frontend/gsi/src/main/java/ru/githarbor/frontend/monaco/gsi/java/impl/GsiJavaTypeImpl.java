package ru.githarbor.frontend.monaco.gsi.java.impl;


import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.GsiIdentifier;
import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.frontend.monaco.gsi.java.GsiJavaType;
import ru.githarbor.shared.JavaElementType;

public class GsiJavaTypeImpl extends BaseGsiElement implements GsiJavaType {

    public GsiJavaTypeImpl(Range range, GsiElement parent) {
        super(range, JavaElementType.TYPE_EXPRESSION, parent);
    }

    @Override
    public String getText() {
        return getName();
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
