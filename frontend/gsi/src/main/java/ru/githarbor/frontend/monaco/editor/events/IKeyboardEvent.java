package ru.githarbor.frontend.monaco.editor.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class IKeyboardEvent {
    public boolean ctrlKey;
}
