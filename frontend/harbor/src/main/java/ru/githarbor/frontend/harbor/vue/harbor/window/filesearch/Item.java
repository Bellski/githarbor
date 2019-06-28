package ru.githarbor.frontend.harbor.vue.harbor.window.filesearch;

public class Item {
    public final String name;
    public final String directory;
    public final String extension;

    public Item(String name, String directory, String extension) {
        this.name = name;
        this.directory = directory;
        this.extension = extension;
    }
}
