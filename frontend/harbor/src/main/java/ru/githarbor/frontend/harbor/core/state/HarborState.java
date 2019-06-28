package ru.githarbor.frontend.harbor.core.state;

import ru.githarbor.frontend.harbor.vue.harbor.window.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HarborState {

    public String currentBranch = null;

    public Window window = null;

    @Inject
    public HarborState() {

    }
}
