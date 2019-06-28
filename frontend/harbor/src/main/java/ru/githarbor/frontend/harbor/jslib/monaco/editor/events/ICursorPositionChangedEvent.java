package ru.githarbor.frontend.harbor.jslib.monaco.editor.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.harbor.jslib.monaco.Position;


@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class ICursorPositionChangedEvent {
    public Position position;
}
