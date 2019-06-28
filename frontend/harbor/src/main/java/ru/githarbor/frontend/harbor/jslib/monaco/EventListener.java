package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface EventListener<E> {
    void on(E event);
}
