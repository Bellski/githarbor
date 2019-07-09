package ru.githarbor.frontend.monaco;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Promise<T> {

    @JsFunction
    public interface Fulfilled<T> {
        void onfulfilled(T data);
    }

    public native Promise<T> then(Fulfilled<T> fulfilled);
}
