package ru.githarbor.frontend.harbor.jslib.monaco;

import jsinterop.annotations.JsProperty;
import jsinterop.base.JsPropertyMap;

import java.util.function.Function;
import java.util.stream.Stream;

public class IModelDeltaDecoration {

    @JsProperty
    public IRange range;

    @JsProperty
    public JsPropertyMap<Object> options;

    public IModelDeltaDecoration(IRange range, String className) {
        this.range = range;
        this.options = JsPropertyMap.of("inlineClassName", className);
    }

    public IModelDeltaDecoration(IRange range, String className, String... hoverMessages) {
        this.range = range;
        this.options = JsPropertyMap.of("inlineClassName", className, "hoverMessage", Stream
                .of(hoverMessages)
                .map((Function<String, Object>) IMarkdownString::new)
                .toArray(IMarkdownString[]::new)
        );
    }
}
