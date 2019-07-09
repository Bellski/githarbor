package ru.githarbor.frontend.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ILanguageExtensionPoint {

    @JsProperty
    public native String getId();

    @JsProperty
    public native String[] getExtensions();
}
