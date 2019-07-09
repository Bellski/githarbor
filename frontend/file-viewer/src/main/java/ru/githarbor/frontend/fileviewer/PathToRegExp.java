package ru.githarbor.frontend.fileviewer;

import elemental2.core.JsArray;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class PathToRegExp {

    @JsMethod(name = "pathToRegexp", namespace = JsPackage.GLOBAL)
    public static native PathToRegExp path(String path);

    public native JsArray<String> exec(String query);
}
