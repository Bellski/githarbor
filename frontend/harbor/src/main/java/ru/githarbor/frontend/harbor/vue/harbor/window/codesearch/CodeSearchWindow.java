package ru.githarbor.frontend.harbor.vue.harbor.window.codesearch;

import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.vue.harbor.window.Window;

public class CodeSearchWindow extends Window {

    public CodeSearchWindow(String directory) {
        super("code-search", JsPropertyMap.of("directory", directory));
    }
}
