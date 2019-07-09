package ru.githarbor.frontend.harbor.jslib;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLTextAreaElement;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;

public class ClipBoard {

    @JsMethod(name = "window.getSelection", namespace = JsPackage.GLOBAL)
    public native static String getSelection();

    @JsMethod(name = "document.execCommand", namespace = JsPackage.GLOBAL)
    public native static void execCommand(String command);

    public static void copy(String text) {
        final HTMLTextAreaElement el = Js.uncheckedCast(DomGlobal.document.createElement("textarea"));
        el.setAttribute("readonly", "");
        el.value = text;
        el.style.position = "absolute";
        el.style.left = "-9999px";

        DomGlobal.document.body.appendChild(el);

        el.select();

        execCommand("copy");
        DomGlobal.document.body.removeChild(el);
    }
}
