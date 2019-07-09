package ru.githarbor.frontend.monaco;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface EventListener<E> {
    void on(E event);
}
