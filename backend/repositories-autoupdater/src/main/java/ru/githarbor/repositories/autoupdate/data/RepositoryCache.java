package ru.githarbor.repositories.autoupdate.data;

import java.util.Arrays;

public class RepositoryCache {
    public static class Branch {
        public String name;
        public long lastCommit;

        @Override
        public String toString() {
            return "Branch{" +
                    "name='" + name + '\'' +
                    ", lastCommit=" + lastCommit +
                    '}';
        }
    }

    public long id;
    public String name;
    public Branch[] branches;

    @Override
    public String toString() {
        return "RepositoryCache{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", branches=" + Arrays.toString(branches) +
                '}';
    }
}
