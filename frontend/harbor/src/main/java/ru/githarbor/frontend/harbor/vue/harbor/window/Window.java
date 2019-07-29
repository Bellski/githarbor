package ru.githarbor.frontend.harbor.vue.harbor.window;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Window {

    public String name;

    public JsPropertyMap props;


    @JsOverlay
    public static Window create(String name) {
        final Window window = new Window();
        window.name = name;

        return window;
    }
}
