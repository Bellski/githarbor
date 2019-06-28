package ru.githarbor.frontend.github.data;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class RateLimit {
    public double limit;
    public double remaining;
    public String resetAt;
}
