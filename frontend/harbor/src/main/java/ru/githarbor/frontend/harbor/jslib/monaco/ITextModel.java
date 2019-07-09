package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.harbor.core.github.core.File;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ITextModel extends ru.githarbor.frontend.monaco.ITextModel {
    @JsProperty
    public native void setGitHubFile(File gitHubFile);

    @JsProperty
    public native File getGitHubFile();
}
