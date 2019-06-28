package ru.githarbor.frontend.harbor.vue.harbor.window.codesearch.item;

public class CodeSearchItem {
    public final String name;
    public final String path;
    public final String extension;
    public boolean resolving = false;
    public boolean resolved = false;
    public double occurrences = 0;

    public MatchLine[] matchLines = null;

    public double currentMatchLine = 0;

    public CodeSearchItem(String name, String path) {
        this.name = name;
        this.path = path;

        if (name.startsWith(".") || name.lastIndexOf(".") == -1) {
            extension =  "text";
        } else {
            extension = name.substring(name.lastIndexOf(".") + 1);
        }
    }
}
