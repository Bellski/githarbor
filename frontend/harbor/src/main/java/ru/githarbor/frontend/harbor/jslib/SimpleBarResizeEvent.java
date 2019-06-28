package ru.githarbor.frontend.harbor.jslib;

import elemental2.core.JsArray;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class SimpleBarResizeEvent extends JsArray<SimpleBarResizeEvent.Pane> {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static final class Pane {
        public double width;
    }
}
