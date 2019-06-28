package ru.githarbor.shared.paths;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Branch {
    public String ownerWithName;
    public String branch;
    public String committedDate;

    @JsOverlay
    public static Branch create(String ownerWithName, String branch, String committedDate) {
        final Branch gitHubBranchDto = new Branch();
        gitHubBranchDto.ownerWithName = ownerWithName;
        gitHubBranchDto.branch = branch;
        gitHubBranchDto.committedDate = committedDate;

        return gitHubBranchDto;
    }
}
