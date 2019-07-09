package ru.githarbor.shared;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class BranchState {
    public String name;
    public String[] expandedNodes;
    public String selectedNode;
    public FileState[] openedFiles;
    public String activeOpenedFile;
}
