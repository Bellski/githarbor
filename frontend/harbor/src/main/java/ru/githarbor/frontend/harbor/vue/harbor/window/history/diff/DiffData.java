package ru.githarbor.frontend.harbor.vue.harbor.window.history.diff;

public class DiffData {
    public final String fileName;
    public final String modified;
    public final String original;

    public DiffData(String fileName, String modified, String original) {
        this.fileName = fileName;
        this.modified = modified;
        this.original = original;
    }
}
