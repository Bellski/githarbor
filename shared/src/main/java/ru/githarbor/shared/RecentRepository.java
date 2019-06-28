package ru.githarbor.shared;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class RecentRepository {
    public String name;
    public long date;
}
