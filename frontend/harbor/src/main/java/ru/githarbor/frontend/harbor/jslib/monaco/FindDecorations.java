package ru.githarbor.frontend.harbor.jslib.monaco;

import elemental2.core.JsArray;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.harbor.jslib.monaco.editor.IEditor;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class FindDecorations implements  Disposable {

    public FindDecorations(IEditor iEditor) {
    }

    public native void set(FindMatch[] findMatch);
    public native void set(JsArray<FindMatch> findMatch);

    @Override
    public native void dispose();
}
