package ru.githarbor.frontend.harbor.test;

import org.junit.Test;
import ru.githarbor.frontend.harbor.vue.harbor.repository.data.PathHelper;

import java.util.HashMap;
import java.util.Map;

public class ParseTest {

    String[] paths = new String[]{"javaparser-maven-sample/src/main/java/com/yourorganization", "javaparser-maven-sample/src/main/resources"};

    @Test
    public void name() {
        PathHelper[] expand = expand("javaparser-maven-sample");

        for (PathHelper pathHelper : expand) {
            expandEmptyDirectories(pathHelper);
        }

        System.out.println();
    }

    void expandEmptyDirectories(PathHelper parent) {
        PathHelper[] expandedPaths = expand(parent.path);

        if (PathHelper.hasOneEmptyDirectory(expandedPaths)) {
            parent.concatenatePath(expandedPaths[0]);

            while (PathHelper.hasOneEmptyDirectory(expandedPaths)) {
                expandedPaths = expand(expandedPaths[0].path);

                if (PathHelper.hasOneEmptyDirectory(expandedPaths)) {
                    parent.concatenatePath(expandedPaths[0]);
                }
            }
        }
    }


    private PathHelper[] expand(String parent) {
        final Map<String, PathHelper> pathHelperByPath = new HashMap<>();

        for (String path : paths) {
            if (path.startsWith(parent + "/") && !path.equals(parent)) {
                int indexOfNextSeparator = path.indexOf("/", parent.length() + 1);
                String childPath = path.substring(0, indexOfNextSeparator > 0 ? indexOfNextSeparator : path.length());

                pathHelperByPath.computeIfAbsent(childPath, s -> new PathHelper(childPath));
            }
        }

        return pathHelperByPath.values()
                .toArray(new PathHelper[0]);
    }
}
