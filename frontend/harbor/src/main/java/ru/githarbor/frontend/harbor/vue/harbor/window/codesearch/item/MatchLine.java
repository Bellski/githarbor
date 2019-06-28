package ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.item;

import elemental2.core.JsArray;
import ru.githarbor.frontend.harbor.jslib.monaco.IRange;

public class MatchLine {
    public String content;
    public final String fileName;
    public final String filePath;
    public final double line;

    public JsArray<IRange> ranges = new JsArray<>();

    public MatchLine(String fileName, String filePath, String content, double line) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.content = content.trim();
        this.line = line;
    }
}
