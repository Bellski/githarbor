package ru.githarbor.frontend.harbor.jslib;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "marked")
public class Marked {
    public static native String parse(String src);
}
