package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "monaco.Selection")
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
