package ru.githarbor.frontend.github.data;

import jsinterop.annotations.JsProperty;
import jsinterop.base.JsPropertyMap;

public class GraphQLRequestBody {

    @JsProperty
    private Object query;

    @JsProperty
    private JsPropertyMap variables;

    public GraphQLRequestBody(Object query, JsPropertyMap variables) {
        this.query = query;
        this.variables = variables;
    }
}
