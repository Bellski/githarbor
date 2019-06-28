package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Promise<T> {

    @JsFunction
    public interface Fulfilled<T> {
        void onfulfilled(T data);
    }

    public native Promise<T> then(Fulfilled<T> fulfilled);
}
