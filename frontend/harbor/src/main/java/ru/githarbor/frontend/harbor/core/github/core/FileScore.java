package ru.githarbor.frontend.harbor.core.github.core;

public class FileScore {
    public final File file;
    public final double score;

    public FileScore(File file, String query) {
        this.file = file;

        final String nameLowerCase = file.canonicalName.toLowerCase();

        if (nameLowerCase.equals(query)) {
            score = 10;
        } else if (nameLowerCase.startsWith(query)) {
            score = 7;
        } else if (nameLowerCase.endsWith(query)) {
            score = 4;
        } else {
            score = 0;
        }
    }
}
