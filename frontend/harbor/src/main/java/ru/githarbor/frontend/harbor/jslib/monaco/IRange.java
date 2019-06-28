package ru.githarbor.frontend.harbor.jslib.monaco;


import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class IRange {

    @JsProperty
    public native double getStartLineNumber();

    @JsProperty
    public native void setStartLineNumber(double startLineNumber);

    @JsProperty
    public native double getEndLineNumber();

    @JsProperty
    public native void setEndLineNumber(double endLineNumber);

    @JsProperty
    public native double getStartColumn();

    @JsProperty
    public native void setStartColumn(double startColumn);

    @JsProperty
    public native double getEndColumn();

    @JsProperty
    public native void setEndColumn(double endColumn);

    @JsOverlay
    public static IRange create(double startLineNumber, double endLineNumber, double startColumn, double endColumn) {
        final IRange iRange = new IRange();
        iRange.setStartLineNumber(startLineNumber);
        iRange.setEndLineNumber(endLineNumber);
        iRange.setStartColumn(startColumn);
        iRange.setEndColumn(endColumn);

        return iRange;
    }

    @JsOverlay
    public static IRange create(double startLineNumber, double startColumn, double endColumn) {
        final IRange iRange = new IRange();
        iRange.setStartLineNumber(startLineNumber);
        iRange.setEndLineNumber(startLineNumber);
        iRange.setStartColumn(startColumn);
        iRange.setEndColumn(endColumn);

        return iRange;
    }
}
