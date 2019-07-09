package ru.githarbor.frontend.fileviewer.core;

public class InitParams {
    public final String ownerWithName;
    public final String branch;
    public final String path;

    public InitParams(String ownerWithName, String branch, String path) {
        this.ownerWithName = ownerWithName;
        this.branch = branch;
        this.path = path;
    }
}
