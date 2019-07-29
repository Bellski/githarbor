package ru.githarbor.frontend.harbor.vue.harbor.window.history.dir;

import elemental2.dom.DomGlobal;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.vue.harbor.window.Window;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class DirectoryHistoryWindow extends Window {

    @JsOverlay
    public static DirectoryHistoryWindow create(String directory) {
        final DirectoryHistoryWindow window = new DirectoryHistoryWindow();
        window.name = "directory-history";
        window.props = JsPropertyMap.of("directory", directory);

        return window;
    }
}
