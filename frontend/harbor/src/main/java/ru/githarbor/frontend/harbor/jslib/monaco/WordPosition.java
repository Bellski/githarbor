package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class WordPosition {

    @JsProperty
    public native String getWord();

    @JsProperty
    public native int getStartColumn();

    @JsProperty
    public native int getEndColumn();
}
