package ru.githarbor.frontend.harbor.vue.component.sidebar;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SideBarState {
    public double sideBarTabIndex = 1;
    public double sideBarTabContentWidth = 20;
    public double sideBarContentWidth = 80;

    @Inject
    public SideBarState() {
    }
}
