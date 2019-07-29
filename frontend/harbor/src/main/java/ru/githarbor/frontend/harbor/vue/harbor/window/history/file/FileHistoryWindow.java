package ru.githarbor.frontend.harbor.vue.harbor.window.history.file;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.vue.harbor.window.Window;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.DirectoryHistoryWindow;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class FileHistoryWindow extends Window {

    @JsOverlay
    public static FileHistoryWindow create(String directory) {
        final FileHistoryWindow window = new FileHistoryWindow();
        window.name = "file-history";
        window.props = JsPropertyMap.of("file", directory);

        return window;
    }
}
