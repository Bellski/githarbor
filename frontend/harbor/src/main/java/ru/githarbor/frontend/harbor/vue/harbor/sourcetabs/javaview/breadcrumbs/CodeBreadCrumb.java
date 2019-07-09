package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.javaview.breadcrumbs;

public class CodeBreadCrumb {
    public final String name;
    public final double line;
    public final double column;

    public CodeBreadCrumb(String name, double line, double column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }
}
