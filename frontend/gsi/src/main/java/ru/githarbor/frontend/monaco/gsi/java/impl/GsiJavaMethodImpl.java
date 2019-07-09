package ru.githarbor.frontend.monaco.gsi.java.impl;

import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.GsiIdentifier;
import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.frontend.monaco.gsi.java.GsiJavaMethod;
import ru.githarbor.shared.JavaElementType;

public class GsiJavaMethodImpl extends BaseGsiElement implements GsiJavaMethod {

    private final boolean isConstructor;

    public GsiJavaMethodImpl(Range range, GsiElement parent, boolean isConstructor) {
        super(range, JavaElementType.METHOD, parent);

        this.isConstructor = isConstructor;
    }

    @Override
    public String getText() {
        return getName() + "()";
    }

    @Override
    public String getName() {
        return getIdentifier().getText();
    }

    @Override
    public GsiIdentifier getIdentifier() {
        return (GsiIdentifier) findChildByType(JavaElementType.NAME);
    }

    @Override
    public boolean isConstructor() {
        return isConstructor;
    }
}
