package ru.githarbor.frontend.harbor.vue.harbor.window.codesearch;

public class Extension {
    public final String name;
    public final String label;

    public Extension(String name) {
        this(name, "*." + name);
    }

    public Extension(String name, String label) {
        this.name = name;
        this.label = label;
    }
}
