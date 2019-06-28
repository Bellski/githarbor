package ru.githarbor.frontend.harbor.vue.harbor.window.history.file;

import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.vue.harbor.window.Window;

public class FileHistoryWindow extends Window {

    public FileHistoryWindow(String filePath) {
        super("file-history", JsPropertyMap.of("file", filePath));
    }
}
