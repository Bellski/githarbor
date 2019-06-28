package ru.githarbor.frontend.harbor.jslib.monaco.editor;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.harbor.jslib.monaco.Disposable;
import ru.githarbor.frontend.harbor.jslib.monaco.ITextModel;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public abstract class StandaloneDiffEditor extends IEditor implements Disposable {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class DiffModel extends ITextModel {
        @JsProperty
        public native void setOriginal(ITextModel model);
        @JsProperty
        public native void setModified(ITextModel model);
    }

    public native void setModel(DiffModel model);

    public native IEditor getModifiedEditor();
    public native IEditor getOriginalEditor();
}
