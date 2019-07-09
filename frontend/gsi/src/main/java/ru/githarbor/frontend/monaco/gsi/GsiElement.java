package ru.githarbor.frontend.monaco.gsi;

import ru.githarbor.shared.JavaElementType;

public interface GsiElement {

    Range getRange();

    String getText();

    GsiElement getParent();

    JavaElementType getType();

    GsiElement[] getChildren();

    GsiElement findChildByType(JavaElementType type);

    GsiElement[] findChildrenByType(JavaElementType type);

    GsiElement findElementStartFrom(double line, double column);
}
