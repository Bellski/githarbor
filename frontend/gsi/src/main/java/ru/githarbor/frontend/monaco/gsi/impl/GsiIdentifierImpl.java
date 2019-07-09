package ru.githarbor.frontend.monaco.gsi.impl;

import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.GsiIdentifier;
import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.frontend.monaco.gsi.java.impl.BaseGsiElement;
import ru.githarbor.shared.JavaElementType;

public class GsiIdentifierImpl extends BaseGsiElement implements GsiIdentifier {

    private final String name;
    private final GsiElement parent;

    public GsiIdentifierImpl(Range range, String name, GsiElement parent) {
        super(range, JavaElementType.NAME, parent);

        this.name = name;
        this.parent = parent;
    }

    @Override
    public String getText() {
        return name;
    }

}
