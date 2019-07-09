package ru.githarbor.frontend.monaco.gsi.java;


import ru.githarbor.frontend.monaco.gsi.ElementWithName;
import ru.githarbor.frontend.monaco.gsi.GsiDeclaration;
import ru.githarbor.frontend.monaco.gsi.GsiElement;

public interface GsiJavaClass extends ElementWithName, GsiDeclaration {
    boolean isStatic();

    GsiElement findFieldByName(String name);
    GsiElement[] findMethodsByName(String name);
    GsiElement findInnerClassByName(String name);
}
