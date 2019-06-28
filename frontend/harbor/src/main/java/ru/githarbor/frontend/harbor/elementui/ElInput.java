package ru.githarbor.frontend.harbor.elementui;

import com.axellience.vuegwt.core.client.component.IsVueComponent;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ElInput implements IsVueComponent {
    public native void focus();
}