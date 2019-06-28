package ru.githarbor.frontend.harbor.elementui;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface TreeNodeResolver<T> {
    Object execute(T[] nodes);
}
