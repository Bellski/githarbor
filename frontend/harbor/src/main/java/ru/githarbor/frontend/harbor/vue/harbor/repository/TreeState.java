package ru.githarbor.frontend.harbor.vue.harbor.repository;

import jsinterop.annotations.JsProperty;

import java.util.HashSet;
import java.util.Set;

public class TreeState {

    @JsProperty
    public Set<String> expandedNodes = new HashSet<>();
}
