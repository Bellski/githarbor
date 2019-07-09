package ru.githarbor.frontend.monaco;

import elemental2.core.JsArray;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class Theme {
    public static JsPropertyMap<Object> LIGHT = JsPropertyMap.of();

    static {
        LIGHT.set("base", "vs");
        LIGHT.set("inherit", true);

        final JsArray<JsPropertyMap<Object>> rules = new JsArray<>();
        rules.push(JsPropertyMap.of("token", "", "foreground", "#313131"));
        rules.push(JsPropertyMap.of("token", "keyword", "foreground", "#000080", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "keyword.control", "foreground", "#000080", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "keyword.operator", "foreground", "#000080", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "keyword.operator.new", "foreground", "#000080", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "keyword.operator.expression", "foreground", "#000080", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "keyword.operator.cast", "foreground", "#000080", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "keyword.operator.sizeof", "foreground", "#000080", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "keyword.operator.instanceof", "foreground", "#000080", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "this.self", "foreground", "#000080", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "string", "foreground", "008000", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "comment", "foreground", "#808080"));
        rules.push(JsPropertyMap.of("token", "type", "foreground", "#313131"));
        rules.push(JsPropertyMap.of("token", "variable", "foreground", "#ff0000"));
        rules.push(JsPropertyMap.of("token", "comment.doc", "foreground", "#808080"));
        rules.push(JsPropertyMap.of("token", "delimiter", "foreground", "#313131"));
        rules.push(JsPropertyMap.of("token", "annotation", "foreground", "#808000"));
        rules.push(JsPropertyMap.of("token", "tag", "foreground", "#000080", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "delimiter.html", "foreground", "#313131"));
        rules.push(JsPropertyMap.of("token", "delimiter.xml", "foreground", "#313131"));
        rules.push(JsPropertyMap.of("token", "delimiter", "foreground", "#313131"));
        rules.push(JsPropertyMap.of("token", "number", "foreground", "#0000FF"));
        rules.push(JsPropertyMap.of("token", "constant", "foreground", "#660E7A", "fontStyle", "italic bold"));
        rules.push(JsPropertyMap.of("token", "constant.language", "foreground", "#660E7A", "fontStyle", "italic bold"));
        rules.push(JsPropertyMap.of("token", "attribute.name", "foreground", "#0000FF", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "attribute.value", "foreground", "#008000", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "attribute.value.html", "foreground", "#008000", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "attribute.value.xml", "foreground", "#008000", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "meta.preprocessor.string", "foreground", "#008000", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "string.key.json", "foreground", "#000080", "fontStyle", "bold"));
        rules.push(JsPropertyMap.of("token", "string.value.json", "foreground", "#008000", "fontStyle", "bold"));

        LIGHT.set("rules", rules);

        final JsPropertyMap<String> colors = Js.uncheckedCast(JsPropertyMap.of());
        colors.set("menu.background", "#F5F5F5");
        colors.set("menu.foreground", "#313131");
        colors.set("menu.selectionBackground", "#ebebeb");
        colors.set("menu.selectionForeground", "#313131");
        colors.set("editor.lineHighlightBackground", "#f2fde4");
        colors.set("editorLineNumber.foreground", "#939393");
        colors.set("editorLineNumber.activeForeground", "#313131");
        colors.set("editorGutter.background", "#F5F5F5");
        colors.set("widget.shadow", "none");



        LIGHT.set("colors", colors);
    }

    public static JsPropertyMap<Object> DARK = JsPropertyMap.of();



    static {
        DARK.set("base", "vs-dark");
        DARK.set("inherit", true);

        final JsArray<JsPropertyMap<Object>> rules = new JsArray<>();
        rules.push(JsPropertyMap.of("token", "", "foreground", "#A9B7C6"));
        rules.push(JsPropertyMap.of("token", "keyword", "foreground", "#CC7832"));
        rules.push(JsPropertyMap.of("token", "string", "foreground", "#6A8759"));
        rules.push(JsPropertyMap.of("token", "comment", "foreground", "#808080"));
        rules.push(JsPropertyMap.of("token", "comment.doc", "foreground", "#6A8759"));
        rules.push(JsPropertyMap.of("token", "delimiter", "foreground", "#A9B7C6"));
        rules.push(JsPropertyMap.of("token", "annotation", "foreground", "#BBB529"));
        rules.push(JsPropertyMap.of("token", "tag", "foreground", "#E8BF6A"));
        rules.push(JsPropertyMap.of("token", "delimiter.html", "foreground", "#E8BF6A"));
        rules.push(JsPropertyMap.of("token", "delimiter.xml", "foreground", "#E8BF6A"));
        rules.push(JsPropertyMap.of("token", "delimiter", "foreground", "#A9B7C6"));
        rules.push(JsPropertyMap.of("token", "number", "foreground", "#6897BB"));
        rules.push(JsPropertyMap.of("token", "constant", "foreground", "#9876AA"));

        DARK.set("rules", rules);

        final JsPropertyMap<String> colors = Js.uncheckedCast(JsPropertyMap.of());
        colors.set("editor.background", "#2B2B2B");
        colors.set("menu.background", "#3c3f41");
        colors.set("menu.foreground", "#CCCCCC");
        colors.set("menu.selectionBackground", "#6e7274");
        colors.set("widget.shadow", "none");
        colors.set("editorGutter.background", "#313335");
        colors.set("editor.lineHighlightBackground", "#323232");
        colors.set("peekView.border", "#5b5e60");
        colors.set("peekViewEditor.background", "#2B2B2B");
        colors.set("peekViewEditorGutter.background", "#313335");
        colors.set("peekViewResult.background", "#2B2B2B");
        colors.set("peekViewTitle.background", "#3c3f41");
        colors.set("peekViewEditor.matchHighlightBackground", "#155221");
        colors.set("peekViewResult.selectionBackground", "#6e7274");
        colors.set("peekViewResult.selectionForeground", "#CCCCCC");
        colors.set("peekViewTitleDescription.foreground", "#CCCCCC");
        colors.set("peekViewTitleLabel.foreground", "#CCCCCC");
        colors.set("peekViewResult.matchHighlightBackground", "0");
        colors.set("input.background", "#45494A");
        colors.set("input.border", "#5b5e60");

        DARK.set("colors", colors);
    }
}
