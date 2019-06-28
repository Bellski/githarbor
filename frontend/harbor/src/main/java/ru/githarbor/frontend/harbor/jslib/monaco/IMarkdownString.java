package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsProperty;

public class IMarkdownString {

    @JsProperty
    public String value;

    public IMarkdownString(String value) {
        this.value = value;
    }
}
