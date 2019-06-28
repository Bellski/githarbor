package ru.githarbor.frontend.harbor.vue.component.menu;

public class Action {
    public final String name;
    public boolean visible;
    public final Runnable run;

    public Action(String name, Runnable run) {
        this(name, true, run);
    }

    public Action(String name, boolean visible, Runnable run) {
        this.name = name;
        this.visible = visible;
        this.run = run;
    }
}
