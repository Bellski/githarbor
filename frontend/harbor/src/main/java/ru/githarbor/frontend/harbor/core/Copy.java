package ru.githarbor.frontend.harbor.core;

import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.jslib.ClipBoard;
import ru.githarbor.frontend.harbor.vue.harbor.repository.data.RepositoryTreeNode;

public class Copy {
    public static void copyNodeName(RepositoryTreeNode repositoryTreeNode) {
        ClipBoard.copy(repositoryTreeNode.name);
    }

    public static void copyNodeUrl(Repository repository, RepositoryTreeNode repositoryTreeNode) {
        String dirOrFile = repositoryTreeNode.leaf ? "/blob/" : "/tree/";

        ClipBoard.copy(
                "http://githarbor.com/" +
                        repository +
                        dirOrFile +
                        repository.getCurrentBranch() +
                        "/" +
                        repositoryTreeNode.key
        );
    }
}
