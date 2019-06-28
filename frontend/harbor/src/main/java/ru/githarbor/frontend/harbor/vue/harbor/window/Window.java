package ru.githarbor.frontend.harbor.vue.harbor.window;

public class Window {
    public final String name;
    public final Object props;

    public Window(String name) {
        this(name, null);
    }

    public Window(String name, Object props) {
        this.name = name;
        this.props = props;
    }
}
