package ru.githarbor.frontend.harbor.core.github.core;

import elemental2.dom.DomGlobal;
import io.reactivex.Single;

public class File {
    public final String name;
    public final String canonicalName;
    public final String extension;
    private String path;
    public final double parent;
    private final Branch branch;

    private String content;

    public File(String name, double parent, Branch branch) {
        this.name = name;
        this.canonicalName = name.substring(0, name.lastIndexOf("."));
        this.parent = parent;
        this.branch = branch;

        if (name.startsWith(".") || name.lastIndexOf(".") == -1) {
            extension =  "text";
        } else {
            extension = name.substring(name.lastIndexOf(".") + 1);
        }


    }

    public String getPath() {
        if (path == null) {
            if (parent == -1) {
                path = name;
            } else {
                path = getParentPath() + "/" + name;
            }
        }

        return path;
    }

    public boolean isContentResolved() {
        return content != null;
    }

    public String getContent() {
        return content;
    }

    protected void setContent(String content) {
        this.content = content;
    }

    public Single<String> resolveContent() {
        if (content != null) {
            return Single.just(content);
        }

        return branch.fileContentRequest.execute(branch.name, branch.ownerWithName.ownerWithName, getPath())
                .doOnSuccess(blob -> {

                    if (blob.text == null) {
                        throw new IllegalStateException("Source is binary");
                    }

                    content = blob.text;
                })
                .map(blob -> blob.text);
    }

    public String getParentPath() {
        if (parent == -1) {
            return null;
        }

        return getParent().path;
    }

    public Directory getParent() {
        if (parent == -1) {
            return null;
        }

        return branch.getDirectories()[Double.valueOf(parent).intValue()];
    }

    public static String extension(String file) {
        if (file.startsWith(".") || file.lastIndexOf(".") == -1) {
            return "text";
        } else {
            return file.substring(file.lastIndexOf(".") + 1);
        }
    }

    public static boolean isJava(File file) {
        return file.extension.equals("java");
    }
}
