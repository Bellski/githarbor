package ru.githarbor.frontend.monaco.gsi.java.impl;

import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.Range;
import ru.githarbor.shared.JavaElementType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

public abstract class BaseGsiElement implements GsiElement {

    private final Range range;
    private final JavaElementType type;
    private final GsiElement parent;
    private GsiElement[] children;

    public BaseGsiElement(Range range, JavaElementType type) {
        this(range, type, null);
    }

    public BaseGsiElement(Range range, JavaElementType type, GsiElement parent) {
        this.range = range;
        this.type = type;
        this.parent = parent;
    }

    @Override
    public Range getRange() {
        return range;
    }

    @Override
    public JavaElementType getType() {
        return type;
    }

    @Override
    public GsiElement getParent() {
        return parent;
    }

    @Override
    public GsiElement[] getChildren() {
        return children;
    }

    public BaseGsiElement setChildren(GsiElement[] children) {
        this.children = (children != null && children.length > 0) ? children : null;

        if (children != null) {
            Arrays.sort(children, Comparator.comparing(GsiElement::getRange));
        }

        return this;
    }

    @Override
    public GsiElement findChildByType(JavaElementType type) {
        return Stream
                .of(children)
                .filter(child -> child.getType() == type)
                .findFirst()
                .orElse(null);
    }

    @Override
    public GsiElement[] findChildrenByType(JavaElementType type) {
        return Stream
                .of(children)
                .filter(child -> child.getType() == type)
                .toArray(GsiElement[]::new);
    }

    @Override
    public GsiElement findElementStartFrom(double line, double column) {
        GsiElement identifier = null;


        if (range.inRange(line, column)) {
            identifier = Stream.of(children)
                    .filter(childElement -> childElement.getChildren() == null && childElement.getRange().equalRange(line, column))
                    .findFirst()
                    .orElse(null);

            if (identifier == null) {
                identifier = Stream.of(children)
                        .filter(childElement -> childElement.getChildren() != null && childElement.getRange().inRange(line, column))
                        .findFirst()
                        .map(childElement -> childElement.findElementStartFrom(line, column))
                        .orElse(null);
            }
        }

        if (identifier != null && identifier.getType() == JavaElementType.NAME) {
            identifier = ((BaseGsiElement) identifier).parent;
        }

        return identifier;
    }

    @Override
    public String toString() {
        return getText() + " " + getRange();
    }
}
