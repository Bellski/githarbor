package ru.githarbor.frontend.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class FindMatch {

    @JsProperty
    public native IRange getRange();

    @JsProperty
    public native void setRange(IRange range);
}
