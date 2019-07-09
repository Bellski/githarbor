package ru.githarbor.frontend.monaco.gsi.java;


import ru.githarbor.frontend.monaco.gsi.ElementWithName;
import ru.githarbor.frontend.monaco.gsi.GsiDeclaration;

public interface GsiJavaMethod extends ElementWithName, GsiDeclaration {
    boolean isConstructor();
}
