package ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.dirtree.data;

import elemental2.core.JsArray;
import jsinterop.annotations.JsProperty;

public class Node {

    @JsProperty
    public final boolean leaf;

    @JsProperty
    public JsArray<FileNode> children;

    public Node(boolean leaf) {
        this.leaf = leaf;

        if (!leaf) {
            this.children = new JsArray<>();
        }
    }

    public RootNode asRootNode() {
        return (RootNode) this;
    }

    public FileNode asFileNode() {
        return (FileNode) this;
    }
}
