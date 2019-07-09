package ru.githarbor.frontend.monaco.editor;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class IDimension {
    public double width;
    public double height;
}
