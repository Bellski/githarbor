package ru.githarbor.frontend.harbor.vue.harbor.repository;

import ru.githarbor.frontend.harbor.core.github.core.Branch;
import ru.githarbor.frontend.harbor.core.github.core.Directory;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.elementui.TreeNode;
import ru.githarbor.frontend.harbor.elementui.TreeNodeResolver;
import ru.githarbor.frontend.harbor.vue.harbor.repository.data.PathHelper;
import ru.githarbor.frontend.harbor.vue.harbor.repository.data.RepositoryTreeNode;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Singleton
public class RepositoryTreeNodeLoader {

    private final Repository repository;

    @Inject
    public RepositoryTreeNodeLoader(Repository repository) {
        this.repository = repository;
    }

    public RepositoryTreeNode[] resolveTreeNodes(TreeNode<RepositoryTreeNode> node, TreeNodeResolver<RepositoryTreeNode> resolver) {
        if (node.data == null) {
            final Branch currentBranch = repository.getCurrentBranch();

            final Map<String, PathHelper> pathHelperByPath = new HashMap<>();

            for (Directory directory : currentBranch.getDirectories()) {
                int indexOfNextSeparator = directory.path.indexOf("/");
                String childPath = directory.path.substring(0, indexOfNextSeparator > 0 ? indexOfNextSeparator : directory.path.length());

                pathHelperByPath.computeIfAbsent(childPath, s -> new PathHelper(childPath));
            }

            final Stream<RepositoryTreeNode> directoriesStream = pathHelperByPath.values()
                    .stream()
                    .map(pathHelper -> {
                        expandEmptyDirectories(pathHelper);

                        return new RepositoryTreeNode(pathHelper.getPathName(),null, pathHelper.path, false);
                    });

            final Stream<RepositoryTreeNode> fileStream = Arrays.stream(currentBranch.getFiles())
                    .filter(file -> file.parent == -1)
                    .map(file -> new RepositoryTreeNode(file.name, file.extension, file.getPath(), true));


            final RepositoryTreeNode[] children = Stream.concat(directoriesStream, fileStream).toArray(RepositoryTreeNode[]::new);

            resolver.execute(children);

            return children;
        }

        node.expanded = true;

        final RepositoryTreeNode[] children = getChildren(node.data.key);

        resolver.execute(children);

        return children;
    }

    public RepositoryTreeNode[] getChildren(String path) {
        return Stream.of(expand(path))
                .map(pathHelper -> {

                    if (pathHelper.directory) {
                        expandEmptyDirectories(pathHelper);
                    }

                    return createTreeNode(pathHelper);
                })
                .toArray(RepositoryTreeNode[]::new);
    }

    private RepositoryTreeNode createTreeNode(PathHelper pathHelper) {
        if (pathHelper.directory) {
            return new RepositoryTreeNode(pathHelper.getPathName(), null, pathHelper.path, false);
        }

        return new RepositoryTreeNode(pathHelper.name, pathHelper.extension, pathHelper.path, true);
    }


    private void expandEmptyDirectories(PathHelper parent) {
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

    private PathHelper[] expand(String pathToExpand) {
        final Directory[] directories = repository.getCurrentBranch().getDirectories();

        final Map<String, PathHelper> pathHelperByPath = new HashMap<>();

        for (Directory directory : directories) {
            final String path = directory.path;

            if (path.startsWith(pathToExpand + "/") && !path.equals(pathToExpand)) {
                int indexOfNextSeparator = path.indexOf("/", pathToExpand.length() + 1);
                String childPath = path.substring(0, indexOfNextSeparator > 0 ? indexOfNextSeparator : path.length());

                pathHelperByPath.computeIfAbsent(childPath, s -> new PathHelper(childPath));
            }
        }

        final Directory directoryToExpand = repository.getCurrentBranch().getDirectory(pathToExpand);

        final File[] files = directoryToExpand != null ? directoryToExpand.getFiles() : new File[0];

        return Stream.concat(
                pathHelperByPath.values().stream(),
                Stream.of(files)
                        .map(file -> new PathHelper(file.getPath(), false))
        ).toArray(PathHelper[]::new);
    }
}
