package ru.githarbor.frontend.harbor.vue.harbor.window.codesearch2;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.vue.harbor.window.Window;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class CodeSearchWindow extends Window {

    @JsOverlay
    public static CodeSearchWindow create(String directory) {
        final CodeSearchWindow window = new CodeSearchWindow();
        window.name = "code-search";
        window.props = JsPropertyMap.of("directory", directory);

        return window;
    }
}
