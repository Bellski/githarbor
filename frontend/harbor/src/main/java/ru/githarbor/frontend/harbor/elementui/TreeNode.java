package ru.githarbor.frontend.harbor.elementui;

import elemental2.core.JsArray;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class TreeNode<T> {
    public T data;
    public boolean expanded;
    public JsArray<TreeNode<T>> childNodes;
    public TreeNode<T> nextSibling;
    public TreeNode<T> previousSibling;
    public TreeNode<T> parent;
    public boolean isLeaf;
    public String key;

    public native void expand();
    public native void collapse();
    public native void click();
}
