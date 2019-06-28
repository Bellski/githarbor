package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Location {
    public IRange range;
    public _URI uri;
}
