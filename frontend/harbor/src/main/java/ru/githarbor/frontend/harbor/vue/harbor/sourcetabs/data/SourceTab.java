package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.data;


import ru.githarbor.frontend.monaco.IRange;

public class SourceTab {
    public final String name;
    public final String key;

    public IRange range;

    public SourceTab(String name, String key) {
        this.name = name;
        this.key = key;
    }
}
