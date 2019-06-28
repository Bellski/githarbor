package ru.githarbor.frontend.harbor.core.github.core;

public class OwnerWithName {
    public final String ownerWithName;
    public final String owner;
    public final String name;

    public OwnerWithName(String ownerWithName) {
        final String[] ownerAndName = ownerWithName.split("/");

        this.ownerWithName = ownerWithName;
        this.owner = ownerAndName[0];
        this.name = ownerAndName[1];
    }
}
