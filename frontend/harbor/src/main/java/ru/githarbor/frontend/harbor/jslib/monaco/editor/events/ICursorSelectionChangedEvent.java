package ru.githarbor.frontend.harbor.jslib.monaco.editor.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.harbor.jslib.monaco.Selection;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class ICursorSelectionChangedEvent {
    public Selection selection;
}
