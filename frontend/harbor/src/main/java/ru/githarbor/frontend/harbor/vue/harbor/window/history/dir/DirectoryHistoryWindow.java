package ru.githarbor.frontend.harbor.vue.harbor.window.history.dir;

import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.vue.harbor.window.Window;

public class DirectoryHistoryWindow extends Window {
    public DirectoryHistoryWindow(String directory) {
        super("directory-history", JsPropertyMap.of("directory", directory));
    }
}
