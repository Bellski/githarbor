package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Position {

    public double lineNumber;
    public double column;

    @JsOverlay
    public static Position create(double lineNumber, double column) {
        final Position position = new Position();
        position.lineNumber = lineNumber;
        position.column = column;

        return position;
    }
}
