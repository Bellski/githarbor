package ru.githarbor.shared;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import java.util.Arrays;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class UiState {
    public String name;
    public double sideBarTabIndex;
    public double sideBarTabContentWidth;
    public double sideBarContentWidth;
    public String currentBranch;
    public BranchState[] branches;

    @JsOverlay
    public final BranchState getBranchState() {
        return Arrays.stream(branches)
                .filter(branchState -> branchState.name.equals(currentBranch))
                .findFirst()
                .orElse(null);
    }

    @JsOverlay
    public final void addBranch(BranchState branchState) {
        final BranchState[] newBranches = Arrays.copyOf(branches, branches.length + 1);
        newBranches[newBranches.length - 1] = branchState;

        branches = newBranches;
    }
}
