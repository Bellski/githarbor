package ru.githarbor.frontend.harbor.jslib;

import elemental2.core.ArrayBuffer;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "pako")
public class Pako {

    public static native Object ungzip(ArrayBuffer arrayBuffer, JsPropertyMap<String> options);

    @JsOverlay
    public static String ungzipToString(ArrayBuffer arrayBuffer) {
        return Js.cast(ungzip(arrayBuffer, Js.cast(JsPropertyMap.of("to", "string"))));
    }
}
