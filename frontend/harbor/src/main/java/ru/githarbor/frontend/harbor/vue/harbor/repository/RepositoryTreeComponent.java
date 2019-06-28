package ru.githarbor.frontend.harbor.vue.harbor.repository;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasDestroyed;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.MouseEvent;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.core.github.core.Branch;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.elementui.ElTree;
import ru.githarbor.frontend.harbor.elementui.TreeNode;
import ru.githarbor.frontend.harbor.elementui.TreeNodeResolver;
import ru.githarbor.frontend.harbor.jslib.MyKeyboardEvent;
import ru.githarbor.frontend.harbor.vue.component.menu.Action;
import ru.githarbor.frontend.harbor.vue.component.menu.ContextMenuComponent;
import ru.githarbor.frontend.harbor.vue.component.menu.Position;
import ru.githarbor.frontend.harbor.vue.harbor.repository.branchselect.BranchSelectComponent;
import ru.githarbor.frontend.harbor.vue.harbor.repository.data.RepositoryTreeNode;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.SourceTabsSharedState;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch2.CodeSearchWindow;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.DirectoryHistoryWindow;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.file.FileHistoryWindow;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static elemental2.dom.DomGlobal.document;

@Component(components = {
        BranchSelectComponent.class,
        ContextMenuComponent.class
})
public class RepositoryTreeComponent implements IsVueComponent, HasCreated, HasDestroyed {

    @Inject
    public User user;

    @Inject
    public Repository repository;

    @Inject
    public RepositoryTreeNodeLoader repositoryTreeNodeLoader;

    @Inject
    public HarborState harborState;

    @Inject
    public SourceTabsSharedState sourceTabsSharedState;

    @Data
    @Inject
    public RepositoryTreeSharedState repositoryTreeSharedState;

    @Inject
    public RepositoryTreeComponentApi repositoryTreeComponentApi;

    @Data
    public boolean resolvingBranch;

    @Data
    public String resolvingBranchProcessMessage = "Resolving branch";

    @Data
    public Action[] contextMenuActions;

    @Data
    public Position contextMenuPosition;

    @Ref
    public ElTree treeComponent;

    private Map<String, TreeState> treeStateByBranch = new HashMap<>();

    public TreeState currentTreeState;

    @Override
    public void created() {
        contextMenuActions = new Action[] {
                new Action("Copy", () -> {

                }),
                new Action("Copy URL", () -> {

                }),
                new Action("Search code", () -> {
                    harborState.window = new CodeSearchWindow(treeComponent.getCurrentNode().key);
                }),
                new Action("History", () -> {
                    if (treeComponent.getCurrentNode().leaf) {
                        harborState.window = new FileHistoryWindow(treeComponent.getCurrentNode().key);

                        return;
                    }

                    harborState.window = new DirectoryHistoryWindow(treeComponent.getCurrentNode().key);
                })
        };

        repositoryTreeComponentApi.setApi(this);
        treeStateByBranch.put(repository.getCurrentBranch().name, currentTreeState = new TreeState());
    }

    @Computed
    public RepositoryTreeNodeLoader getRepositoryTreeNodeLoader() {
        return repositoryTreeNodeLoader;
    }

    @Computed
    public boolean getIsTier1User() {
        return user.tier1Backer;
    }

    @Computed
    public boolean getInFavorites() {
        return repositoryTreeSharedState.inFavorites;
    }

    @JsMethod
    public void resolveTreeNodes(TreeNode<RepositoryTreeNode> node, TreeNodeResolver<RepositoryTreeNode> resolver) {
        final RepositoryTreeNode[] resolved = repositoryTreeNodeLoader.resolveTreeNodes(node, resolver);

        vue().$nextTick(() -> {
            if (node.data == null && treeComponent.getCurrentKey() == null) {
                treeComponent.setCurrentKey(resolved[0].key);
            }
        });
    }

    @JsMethod
    protected void onKeyNavigation(Event evt) {
        evt.stopPropagation();

        final MyKeyboardEvent keyboardEvent = Js.cast(evt);
        final int keyCode = keyboardEvent.getKeyCode();

        final TreeNode<RepositoryTreeNode> treeNode = treeComponent.getNode(treeComponent.getCurrentKey());

        switch (keyCode) {
            case 38: { //up
                TreeNode<RepositoryTreeNode> nodeToSelect = treeNode.previousSibling;

                if (nodeToSelect == null && treeNode.parent.key != null) {
                    nodeToSelect = treeNode.parent;
                } else if (nodeToSelect != null && !nodeToSelect.isLeaf && nodeToSelect.expanded) {
                    nodeToSelect = nodeToSelect.childNodes.getAt(nodeToSelect.childNodes.length - 1);
                }

                if (nodeToSelect != null && nodeToSelect.key != null) {
                    treeComponent.setCurrentKey(nodeToSelect.key);

                    scrollToNearestNode(nodeToSelect);
                }
            }
            break;
            case 40: {// down
                final TreeNode<RepositoryTreeNode> nodeToSelect;

                if (treeNode.expanded) {
                    nodeToSelect = treeNode.childNodes.getAt(0);
                } else if (treeNode.nextSibling == null) {
                    nodeToSelect = treeNode.parent.nextSibling;
                } else {
                    nodeToSelect = treeNode.nextSibling;
                }

                if (nodeToSelect != null && nodeToSelect.key != null) {
                    treeComponent.setCurrentKey(nodeToSelect.key);

                    scrollToNearestNode(nodeToSelect);
                }
            }
            break;
            case 37: { //left
                TreeNode<RepositoryTreeNode> nodeToSelect = null;

                if (treeNode.isLeaf) {
                    if (treeNode.previousSibling != null) {
                        nodeToSelect = treeNode.parent;
                    } else {
                        TreeNode<RepositoryTreeNode> parent = treeNode.parent;

                        while (parent != null) {
                            if (parent.previousSibling == null) {
                                parent = parent.parent;
                            } else {
                                nodeToSelect = parent.previousSibling;

                                break;
                            }
                        }
                    }
                } else {
                    if (treeNode.expanded) {
                        collapseNode(treeNode);
                    } else {
                        if (treeNode.parent.key != null) {
                            nodeToSelect = treeNode.parent;
                        }
                    }
                }

                if (nodeToSelect != null && nodeToSelect.key != null) {
                    treeComponent.setCurrentKey(nodeToSelect.key);

                    scrollToNearestNode(nodeToSelect);
                }
            }
            break;
            case 39: { // right
                TreeNode<RepositoryTreeNode> nodeToSelect = null;

                if (treeNode.isLeaf) {
                    if (treeNode.nextSibling != null) {
                        nodeToSelect = treeNode.nextSibling;
                    } else {
                        TreeNode<RepositoryTreeNode> parent = treeNode.parent;

                        while (parent != null) {
                            if (parent.nextSibling == null) {
                                parent = parent.parent;
                            } else {
                                nodeToSelect = parent.nextSibling;

                                break;
                            }
                        }
                    }
                } else {
                    if (treeNode.expanded) {
                        nodeToSelect = treeNode.childNodes.getAt(0);
                    } else {
                        expandNode(treeNode);
                    }
                }

                if (nodeToSelect != null && nodeToSelect.key != null) {
                    treeComponent.setCurrentKey(nodeToSelect.key);

                    scrollToNearestNode(nodeToSelect);
                }
            }
            break;
            case 13: {
                if (treeNode.isLeaf) {

                } else {
                    if (treeNode.expanded) {
                        collapseNode(treeNode);
                    } else {
                        expandNode(treeNode);
                    }
                }
            }
        }
    }

    @JsMethod
    public void onNodeCollapse(RepositoryTreeNode repositoryTreeNode, TreeNode<RepositoryTreeNode> treeNode) {
        final String[] toCollapse = currentTreeState.expandedNodes.stream()
                .filter(nodeKey -> nodeKey.startsWith(treeNode.data.key))
                .toArray(String[]::new);

        for (String toCollapseKey : toCollapse) {
            if (toCollapseKey.startsWith(treeNode.data.key)) {
                treeComponent.getNode(toCollapseKey).collapse();

                currentTreeState.expandedNodes.remove(toCollapseKey);
            }
        }
    }

    @JsMethod
    public void onNodeExpand(RepositoryTreeNode repositoryTreeNode, TreeNode<RepositoryTreeNode> treeNode) {
        currentTreeState.expandedNodes.add(treeNode.key);
    }

    @JsMethod
    private void scrollToNearestNode(TreeNode<RepositoryTreeNode> node) {
        document.getElementById(node.key).scrollIntoView(Element.ScrollIntoViewTopUnionType.of(
                JsPropertyMap.of("block", "nearest")
        ));
    }

    public void expandNode(TreeNode<RepositoryTreeNode> treeNode) {
        if (!treeNode.isLeaf && !treeNode.expanded) {
            treeNode.expand();

            onNodeExpand(treeNode.data, treeNode);
        }
    }

    public void collapseNode(TreeNode<RepositoryTreeNode> treeNode) {
        if (!treeNode.isLeaf && treeNode.expanded) {
            treeNode.collapse();

            onNodeCollapse(treeNode.data, treeNode);
        }
    }

    public void revealKey(String key) {
        final String[] keyParts = key.split("/");

        final StringJoiner path = new StringJoiner("/");

        for (int i = 0; i < keyParts.length - 1; i++) {
            path.add(keyParts[i]);

            final TreeNode<RepositoryTreeNode> elNode = treeComponent.getNode(path.toString());

            if (elNode != null) {
                elNode.expand();
            }
        }

        vue().$nextTick(() -> {
            final TreeNode<RepositoryTreeNode> nodeToReveal = treeComponent.getNode(key);

            treeComponent.setCurrentKey(key);

            scrollToNearestNode(nodeToReveal);

            if (!nodeToReveal.isLeaf) {
                nodeToReveal.expand();
            }
        });
    }

    @JsMethod
    public void onBranchChange(String branchName) {
        resolvingBranch = true;

        vue().$nextTick(() -> {
            final Branch newBranch = repository.setCurrentBranch(branchName);

            if (newBranch.isResolved()) {
                currentTreeState = treeStateByBranch.computeIfAbsent(branchName, key -> new TreeState());

                resolvingBranch = false;

                harborState.currentBranch = branchName;
            } else {
                newBranch.resolve(message -> {
                    switch (message) {
                        case "0":
                            this.resolvingBranchProcessMessage = "Caching the new repository";
                            break;
                        case "1":
                            this.resolvingBranchProcessMessage = "Caching the new branch";
                            break;
                        case "2":
                            this.resolvingBranchProcessMessage = "Updating the branch";
                            break;
                        case "3":
                            this.resolvingBranchProcessMessage = "Caching the huge repository, please wait";
                            break;
                    }
                }).subscribe(() -> {
                    resolvingBranch = false;

                    harborState.currentBranch = branchName;
                });
            }
        });
    }

    @JsMethod
    public void onFileDoubleClick(RepositoryTreeNode repositoryTreeNode) {
        repository.getCurrentBranch()
                .getFile(repositoryTreeNode.key)
                .ifPresent(file -> sourceTabsSharedState.addSourceTab(file));
    }

    @JsMethod
    public void onFavoriteClick() {
        repositoryTreeSharedState.inFavorites = repository.setFavorite();
    }


    @JsMethod
    public void onContextMenu(MouseEvent mouseEvent, RepositoryTreeNode node) {
        treeComponent.setCurrentKey(node.key);

        contextMenuActions[2].visible = !node.leaf;

        contextMenuPosition = new Position(mouseEvent.clientX, mouseEvent.clientY);
    }

    @Override
    public void destroyed() {
        repositoryTreeComponentApi.setApi(null);
        repositoryTreeComponentApi = null;
    }
}
