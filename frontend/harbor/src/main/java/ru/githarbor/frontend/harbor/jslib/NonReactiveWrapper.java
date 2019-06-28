package ru.githarbor.frontend.harbor.jslib;

import elemental2.core.JsObject;
import jsinterop.base.JsPropertyMap;

public class NonReactiveWrapper<T> {

    private T data;

    private NonReactiveWrapper(T data) {
        this.data = data;
        JsObject.defineProperty(
                this,
                "_isVue",
                JsPropertyMap.of("value", true, "enumerable", false, "configurable", true)
        );
    }

    public T get() {
        return data;
    }

    public static <T> NonReactiveWrapper<T> of(T data) {
        return new NonReactiveWrapper<>(data);
    }
}
