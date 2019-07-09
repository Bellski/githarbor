package ru.githarbor.frontend.monaco.editor.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.monaco.Position;


@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ICursorPositionChangedEvent {
    public Position position;
}
