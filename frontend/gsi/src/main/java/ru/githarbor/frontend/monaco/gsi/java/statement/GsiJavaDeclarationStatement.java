package ru.githarbor.frontend.monaco.gsi.java.statement;

import ru.githarbor.frontend.monaco.gsi.GsiElement;
import ru.githarbor.frontend.monaco.gsi.java.GsiJavaStatement;


public interface GsiJavaDeclarationStatement extends GsiJavaStatement {
    GsiElement findVariableByName(String name);
}
