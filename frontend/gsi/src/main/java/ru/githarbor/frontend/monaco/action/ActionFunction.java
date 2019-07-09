package ru.githarbor.frontend.monaco.action;

import jsinterop.annotations.JsFunction;
import ru.githarbor.frontend.monaco.editor.IEditor;


@JsFunction
@FunctionalInterface
public interface ActionFunction {
    void run(IEditor editor);
}
