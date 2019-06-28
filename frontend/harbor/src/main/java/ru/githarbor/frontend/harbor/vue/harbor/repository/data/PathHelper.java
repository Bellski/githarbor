package ru.githarbor.frontend.harbor.vue.harbor.repository.data;

public class PathHelper {


    public String name;
    public String extension;
    public String path;
    public String directoryPath;
    public final boolean directory;

    public PathHelper(String path) {
        this(path, true);
    }

    public PathHelper(String path, boolean directory) {
        this.name = path.substring(path.lastIndexOf("/") + 1);

        if (!directory) {
            extension = name.startsWith(".") || name.lastIndexOf(".") == -1 ? "text" : name.substring(name.lastIndexOf(".") + 1);
        }

        this.path = path;
        this.directoryPath = path.substring(0, path.length() - name.length() - 1);
        this.directory = directory;
    }

    public static boolean hasOneEmptyDirectory(PathHelper[] children) {
        return children.length == 1 && children[0].directory;
    }

    public String getPathName() {
        return name;
    }

    public void concatenatePath(PathHelper path) {
        this.name += "/" + path.name;
        this.path = path.path;
    }
}
