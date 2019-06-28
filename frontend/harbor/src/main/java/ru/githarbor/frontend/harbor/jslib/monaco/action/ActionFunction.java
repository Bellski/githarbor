package ru.githarbor.frontend.harbor.jslib.monaco.action;

import jsinterop.annotations.JsFunction;
import ru.githarbor.frontend.harbor.jslib.monaco.editor.IEditor;


@JsFunction
@FunctionalInterface
public interface ActionFunction {
    void run(IEditor editor);
}
