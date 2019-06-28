package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class ILanguageExtensionPoint {

    @JsProperty
    public native String getId();

    @JsProperty
    public native String[] getExtensions();
}
