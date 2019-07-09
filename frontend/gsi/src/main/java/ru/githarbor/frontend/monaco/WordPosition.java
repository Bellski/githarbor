package ru.githarbor.frontend.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class WordPosition {

    @JsProperty
    public native String getWord();

    @JsProperty
    public native int getStartColumn();

    @JsProperty
    public native int getEndColumn();
}
