package ru.githarbor.frontend.hello.vue;

import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;

public class HarborGlobal {

    @JsProperty(name = "document.activeElement", namespace = JsPackage.GLOBAL)
    public static native HTMLElement getActiveElement();

    @JsMethod(namespace = JsPackage.GLOBAL)
    public static native String kFormat(double num);

    @JsMethod(name = "window.timeago", namespace = JsPackage.GLOBAL)
    public native static String timeAgo(String date, String locale);

    @JsMethod(name = "window.timeago", namespace = JsPackage.GLOBAL)
    public native static String timeAgo(Object date);

    @JsMethod(name = "window.timeago2", namespace = JsPackage.GLOBAL)
    public native static String timeAgo2(double date);

    @JsMethod(name = "window.highlight", namespace = JsPackage.GLOBAL)
    public native static String highlight(String query, String content);
}
