package ru.githarbor.frontend.monaco;

import jsinterop.annotations.JsProperty;

public class IMarkdownString {

    @JsProperty
    public String value;

    public IMarkdownString(String value) {
        this.value = value;
    }
}
