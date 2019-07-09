package ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.item;

import elemental2.core.JsArray;
import elemental2.core.JsRegExp;
import elemental2.core.JsString;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import ru.githarbor.frontend.monaco.IRange;
import ru.githarbor.frontend.monaco.Monaco;

import static elemental2.dom.DomGlobal.document;

public class MatchLine {
    public String content;
    public final String fileName;
    public final String filePath;
    public final double line;

    public JsArray<IRange> ranges = new JsArray<>();

    public MatchLine(String fileName, String filePath, String lang, String content, String query, double line) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.content = content.trim();
        this.line = line;

        Monaco.colorize(content, lang).then(p0 -> {
            final HTMLElement tempElement = Js.cast(document.createElement("span"));
            tempElement.innerHTML = p0;

            final HTMLElement spanWithEmptySpaces = Js.cast(tempElement.querySelectorAll("span").item(1));
            spanWithEmptySpaces.innerHTML = spanWithEmptySpaces.innerHTML.replaceAll("^(&nbsp;)+", "");

            final JsRegExp re = new JsRegExp(query.replace("/[.*+?^${}()|[\\]\\\\]/g", "\\$&"), "gi");

            tempElement.innerHTML = new JsString(tempElement.innerHTML).replace(re, "<span class=\"g-code-search-mark\">$&</span>");

            this.content = tempElement.innerHTML;

        });
    }
}
