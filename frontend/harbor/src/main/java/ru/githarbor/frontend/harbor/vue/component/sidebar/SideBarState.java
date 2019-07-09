package ru.githarbor.frontend.harbor.vue.component.sidebar;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SideBarState {

    public double oldSideBarTabContentWidth = 20;
    public double oldSideBarContentWidth = 80;

    public double sideBarTabIndex = 1;
    public double sideBarTabContentWidth = 20;
    public double sideBarContentWidth = 80;

    @Inject
    public SideBarState() {
    }

    public void setSideBarTabContentWidth(double sideBarTabContentWidth) {
        this.sideBarTabContentWidth = sideBarTabContentWidth;
        this.oldSideBarTabContentWidth = sideBarTabContentWidth;
    }

    public void setSideBarContentWidth(double sideBarContentWidth) {
        this.sideBarContentWidth = sideBarContentWidth;
        this.oldSideBarContentWidth = sideBarContentWidth;
    }
}
