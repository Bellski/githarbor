package ru.githarbor.frontend.monaco.gsi.java.impl.variable;


import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.GsiIdentifier;
import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.frontend.monaco.gsi.java.impl.BaseGsiElement;
import ru.githarbor.frontend.monaco.gsi.java.variable.GsiJavaVariable;
import ru.githarbor.shared.JavaElementType;

public class GsiJavaVariableImpl extends BaseGsiElement implements GsiJavaVariable {

    public GsiJavaVariableImpl(Range range, JavaElementType type, GsiElement parent) {
        super(range, type, parent);
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
