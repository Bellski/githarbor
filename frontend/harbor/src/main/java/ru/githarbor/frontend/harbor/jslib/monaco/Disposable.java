package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface Disposable {
    void dispose();
}
