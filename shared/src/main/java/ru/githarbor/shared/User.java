package ru.githarbor.shared;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class User {
    public long id;
    public boolean darkTheme;
    public boolean tier2Backer;
    public boolean tier1Backer;
    public String[] favoriteRepositories;
    public RecentRepository[] recentRepositories;
    public UiState uiState;
    public String accessToken;

    @JsOverlay
    public final String getTheme() {
        return darkTheme ? "dark" : "light";
    }


}
