package ru.githarbor.frontend.harbor.vue.harbor.repository.branchselect;

import jsinterop.annotations.JsProperty;

public class BranchOption {

    @JsProperty
    public String name;

    public BranchOption(String name) {
        this.name = name;
    }
}
