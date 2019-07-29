package ru.githarbor.frontend.harbor.core.rpc.go;

import elemental2.core.JsArray;
import elemental2.core.JsNumber;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class GoSourceMetadata {
    public JsArray<JsNumber>[] ids;
    public JsArray<JsNumber>[] resolved;
}
