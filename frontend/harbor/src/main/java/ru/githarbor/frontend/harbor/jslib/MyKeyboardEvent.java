package ru.githarbor.frontend.harbor.jslib;

import elemental2.dom.KeyboardEvent;
import elemental2.dom.KeyboardEventInit;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class MyKeyboardEvent extends KeyboardEvent {

    @JsProperty
    public native int getKeyCode();

    @JsMethod
    public native void preventDefault();

    public MyKeyboardEvent(String type, KeyboardEventInit eventInitDict) {
        super(type, eventInitDict);
    }

    public MyKeyboardEvent(String type) {
        super(type);
    }
}
