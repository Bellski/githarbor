package ru.githarbor.frontend.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Selection extends IRange {
    public int endColumn;
    public int endLineNumber;
    public int positionColumn;
    public int positionLineNumber;
    public int selectionStartColumn;
    public int selectionStartLineNumber;
    public int startColumn;
    public int startLineNumber;
}
