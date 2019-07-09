package ru.githarbor.frontend.harbor.core;


import ru.githarbor.frontend.monaco.IRange;

public class InitParameters {
    public final String ownerWithName;
    public final String branch;
    public final String path;
    public boolean blob;
    public IRange selection;

    public InitParameters() {
        this(null, null, null, false);
    }

    public InitParameters(String ownerWithName) {
        this(ownerWithName, null, null, false);
    }

    public InitParameters(String ownerWithName, String branch, String path, boolean blob) {
        this.ownerWithName = ownerWithName;
        this.branch = branch;
        this.path = path;
        this.blob = blob;
    }
}
