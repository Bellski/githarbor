package ru.githarbor.frontend.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;


@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ITextModel implements Disposable {
    public native void setValue(String value);
    public native String getValue();
    public native String getValueInRange(IRange iRange);
    public native FindMatch[] findMatches(String query);
    public native String getLineContent(double line);
    public native WordPosition getWordAtPosition(Position position);
    public native int getLineLastNonWhitespaceColumn(double line);

    @JsProperty
    public native _URI getUri();

    @Override
    public native void dispose();
}
