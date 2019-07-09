package ru.githarbor.frontend.harbor.vue.component.menu;

public class Action {
    public final String name;
    public final String shortCut;
    public boolean visible;
    public final Runnable run;

    public Action(String name, String shortCut, Runnable run) {
        this(name, shortCut, true, run);
    }

    public Action(String name, Runnable run) {
        this(name, null, true, run);
    }

    public Action(String name, String shortCut, boolean visible, Runnable run) {
        this.name = name;
        this.shortCut = shortCut;
        this.visible = visible;
        this.run = run;
    }
}
