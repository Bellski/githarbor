package ru.githarbor.frontend.monaco.gsi.java.expression;


import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.GsiReference;

public interface GsiJavaReferenceExpression extends GsiJavaExpression, GsiReference {
    GsiElement[] resolve();
}
