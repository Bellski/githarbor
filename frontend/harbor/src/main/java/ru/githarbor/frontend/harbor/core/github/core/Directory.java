package ru.githarbor.frontend.harbor.core.github.core;

import elemental2.core.JsArray;

import java.util.Arrays;
import java.util.Optional;

public class Directory {

    public final double index;

    public final String path;

    private final Branch branch;

    final JsArray<Double> fileIndexes = new JsArray<>();

    public Directory(double index, String path, Branch branch) {
        this.index = index;
        this.path = path;
        this.branch = branch;
    }

    public File[] getFiles() {
        final File[] files = new File[fileIndexes.length];

        for (int i = 0; i < fileIndexes.length; i++) {
            files[i] = branch.getFiles()[fileIndexes.getAt(i).intValue()];
        }

        return files;
    }

    public Optional<File> getFile(String fileName) {
        return Arrays.stream(getFiles())
                .filter(file -> file.name.equals(fileName))
                .findFirst();
    }
}
