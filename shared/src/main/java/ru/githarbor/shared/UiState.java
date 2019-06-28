package ru.githarbor.shared;

import java.util.Arrays;

public class UiState {
    public String name;
    public int sideBarTabIndex;
    public double sideBarTabContentWidth;
    public double sideBarContentWidth;
    public String currentBranch;
    public BranchState[] branches;

    @Override
    public String toString() {
        return "UiState{" +
                "name='" + name + '\'' +
                ", sideBarTabIndex=" + sideBarTabIndex +
                ", sideBarTabContentWidth=" + sideBarTabContentWidth +
                ", sideBarContentWidth=" + sideBarContentWidth +
                ", currentBranch='" + currentBranch + '\'' +
                ", branches=" + Arrays.toString(branches) +
                '}';
    }
}
