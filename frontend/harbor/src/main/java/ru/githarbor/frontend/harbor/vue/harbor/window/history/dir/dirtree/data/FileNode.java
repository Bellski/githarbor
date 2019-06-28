package ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.dirtree.data;

import ru.githarbor.frontend.harbor.core.github.request.CommitFilesRequest;

public class FileNode extends Node {

    public final String fullPath;
    public final String name;
    public final String path;
    public final String status;

    public FileNode(CommitFilesRequest.CommitFile commitFile) {
        super(true);

        this.fullPath = commitFile.filename;
        this.name = commitFile.filename.substring(commitFile.filename.lastIndexOf("/") + 1);
        this.path = commitFile.filename.substring(0, commitFile.filename.lastIndexOf("/"));
        this.status = commitFile.status;
    }
}
