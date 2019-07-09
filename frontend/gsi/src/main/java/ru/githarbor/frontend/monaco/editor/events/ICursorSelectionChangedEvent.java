package ru.githarbor.frontend.monaco.editor.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.monaco.Selection;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ICursorSelectionChangedEvent {
    public Selection selection;
}
