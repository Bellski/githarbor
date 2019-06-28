package ru.githarbor.frontend.harbor.vue.harbor.repository.data;

import jsinterop.annotations.JsProperty;

public class RepositoryTreeNode {

    public final String name;

    public final String extension;

    @JsProperty
    public final String key;

    @JsProperty
    public boolean leaf;

    public RepositoryTreeNode(String name, String extension, String key, boolean leaf) {
        this.name = name;
        this.extension = extension;
        this.key = key;
        this.leaf = leaf;
    }
}
