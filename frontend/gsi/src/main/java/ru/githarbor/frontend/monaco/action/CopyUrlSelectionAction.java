package ru.githarbor.frontend.monaco.action;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLTextAreaElement;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import ru.githarbor.frontend.monaco.Monaco;
import ru.githarbor.frontend.monaco.Selection;

public class CopyUrlSelectionAction extends Action {

    public CopyUrlSelectionAction(String ownerWithName, String branch, String path) {
        super(
                "ig.copyUrl",
                "Copy Url",
                "",
                "9_cutcopypaste",
                editor -> {
                    final Selection selection = editor.getSelection();

                    final StringBuilder urlBuilder = new StringBuilder().append("http://githarbor.com/")
                            .append(ownerWithName)
                            .append("/blob/")
                            .append(branch)
                            .append("/")
                            .append(path)
                            .append("/#")
                            .append("L").append(selection.getStartLineNumber())
                            .append("-")
                            .append("L").append(selection.getEndLineNumber())
                            .append(",")
                            .append("C").append(selection.getStartColumn())
                            .append("-")
                            .append("C").append(selection.getEndColumn());

                    copy(urlBuilder.toString());

                    editor.focus();
                },
                Monaco.KeyMod.CtrlCmd | Monaco.KeyMod.Shift | Monaco.KeyCode.KEY_C
        );
    }

    @JsMethod(name = "window.getSelection", namespace = JsPackage.GLOBAL)
    public native static String getSelection();

    @JsMethod(name = "document.execCommand", namespace = JsPackage.GLOBAL)
    public native static void execCommand(String command);

    private static void copy(String text) {
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
