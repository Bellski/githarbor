package ru.githarbor.frontend.harbor.vue.harbor.repository;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasDestroyed;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.MouseEvent;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.core.Copy;
import ru.githarbor.frontend.harbor.core.github.core.Branch;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.elementui.ElTree;
import ru.githarbor.frontend.harbor.elementui.TreeNode;
import ru.githarbor.frontend.harbor.elementui.TreeNodeResolver;
import ru.githarbor.frontend.harbor.event.Events;
import ru.githarbor.frontend.harbor.jslib.ClipBoard;
import ru.githarbor.frontend.harbor.jslib.MyKeyboardEvent;
import ru.githarbor.frontend.harbor.vue.component.menu.Action;
import ru.githarbor.frontend.harbor.vue.component.menu.ContextMenuComponent;
import ru.githarbor.frontend.harbor.vue.component.menu.Position;
import ru.githarbor.frontend.harbor.vue.harbor.repository.branchselect.BranchSelectComponent;
import ru.githarbor.frontend.harbor.vue.harbor.repository.data.RepositoryTreeNode;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.SourceTabsSharedState;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.SourceTabsState;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch2.CodeSearchWindow;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.DirectoryHistoryWindow;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.file.FileHistoryWindow;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;
import ru.githarbor.shared.BranchState;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static elemental2.dom.DomGlobal.document;
import static elemental2.dom.DomGlobal.fetch;

@Component(components = {
        BranchSelectComponent.class,
        ContextMenuComponent.class,
        LoaderComponent.class
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

    @Data
    public TreeState currentTreeState;

    @Ref
    public ElTree treeComponent;

    private Map<String, TreeState> treeStateByBranch = new HashMap<>();

    private String defaultSelectKey;

    @Override
    public void created() {
        vue().$root().vue().$on(Events.GLOBAL_KEYDOWN, parameter -> {
            final MyKeyboardEvent keyboardEvent = Js.cast(parameter);

            if (keyboardEvent.ctrlKey) {
                return;
            }

            if (keyboardEvent.altKey && keyboardEvent.getKeyCode() == 84) {
                vue().$el().focus();
            }
        });

        contextMenuActions = new Action[] {
                new Action("Copy", "Ctrl + C", this::copyTreeNodeNameToClipBoard),
                new Action("Copy URL","Alt + C", this::copyFileUrlToClipBoard),
                new Action("Search code", "Alt + D", this::openCodeSearch),
                new Action("History", "Alt + A", this::openHistory)
        };

        repositoryTreeComponentApi.setApi(this);
        treeStateByBranch.put(repository.getCurrentBranch().name, currentTreeState = new TreeState());

        if (user.tier1Backer) {
            final String[] expandedNodes = user.uiState.getBranchState().expandedNodes;
            final String selectedNode = user.uiState.getBranchState().selectedNode;

            if (expandedNodes != null) {
                currentTreeState.expandedNodes.addAll(Arrays.asList(expandedNodes));
            }

            if (selectedNode != null) {
                defaultSelectKey = selectedNode;
            }
        }
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

    @Computed
    public String[] getExpandedNodeKeys() {
        return currentTreeState.expandedNodes.toArray(new String[0]);
    }

    @JsMethod
    public void resolveTreeNodes(TreeNode<RepositoryTreeNode> node, TreeNodeResolver<RepositoryTreeNode> resolver) {
        final RepositoryTreeNode[] resolved = repositoryTreeNodeLoader.resolveTreeNodes(node, resolver);

        vue().$nextTick(() -> {
            if (node.data == null && treeComponent.getCurrentKey() == null) {
                String toSelect = defaultSelectKey != null ? defaultSelectKey : resolved[0].key;
                treeComponent.setCurrentKey(toSelect);

                vue().$nextTick(() -> {
                    scrollToNearestNode(treeComponent.getNode(toSelect));
                });
            }
        });
    }

    @JsMethod
    protected void onKeyNavigation(Event evt) {

        final MyKeyboardEvent keyboardEvent = Js.cast(evt);
        final int keyCode = keyboardEvent.getKeyCode();

        final TreeNode<RepositoryTreeNode> treeNode = treeComponent.getNode(treeComponent.getCurrentKey());


        if (keyboardEvent.altKey && keyboardEvent.getKeyCode() == 67) {
            keyboardEvent.preventDefault();

            if (treeNode.isLeaf) {
                copyFileUrlToClipBoard();
            }

            return;
        }

        if (keyboardEvent.altKey) {
            keyboardEvent.preventDefault();

            if (keyboardEvent.getKeyCode() == 67) {
                if (treeNode.isLeaf) {
                    copyFileUrlToClipBoard();
                }

                return;
            }

            if (keyboardEvent.getKeyCode() == 68) {
                if (!treeNode.isLeaf) {
                    openCodeSearch();
                }

                return;
            }

            if (keyboardEvent.getKeyCode() == 65) {
                openHistory();

                return;
            }
        }

        switch (keyCode) {
            case 38: { //up
                evt.preventDefault();

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
                evt.preventDefault();

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
                evt.preventDefault();

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
                evt.preventDefault();

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
                evt.preventDefault();

                if (treeNode.isLeaf) {
                    final File file = repository
                            .getCurrentBranch()
                            .getFile(treeNode.key)
                            .get();

                    sourceTabsSharedState.addSourceTab(file);

                } else {
                    if (treeNode.expanded) {
                        collapseNode(treeNode);
                    } else {
                        expandNode(treeNode);
                    }
                }
            }
            break;
            case 67: {
                evt.preventDefault();

                if (keyboardEvent.ctrlKey && keyboardEvent.shiftKey) {
                    keyboardEvent.preventDefault();

                    copyTreeNodeNameToClipBoard();
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

        user.uiState.getBranchState().expandedNodes = currentTreeState.expandedNodes.toArray(new String[0]);
    }

    @JsMethod
    public void onNodeExpand(RepositoryTreeNode repositoryTreeNode, TreeNode<RepositoryTreeNode> treeNode) {
        currentTreeState.expandedNodes.add(treeNode.key);

        user.uiState.getBranchState().expandedNodes = currentTreeState.expandedNodes.toArray(new String[0]);
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

            if (!nodeToReveal.isLeaf) {
                nodeToReveal.expand();
            }

            vue().$nextTick(() -> {
                document.getElementById(nodeToReveal.key).scrollIntoView(Element.ScrollIntoViewTopUnionType.of(
                        JsPropertyMap.of("block", "center")
                ));
            });
        });
    }

    @JsMethod
    public void onBranchChange(String branchName) {
        resolvingBranch = true;

        vue().$nextTick(() -> {
            final Branch newBranch = repository.setCurrentBranch(branchName);

            if (user.tier1Backer) {
                user.uiState.currentBranch = branchName;
            }

            if (newBranch.isResolved()) {
                harborState.currentBranch = branchName;

                currentTreeState = treeStateByBranch.get(branchName);

                resolvingBranch = false;
            } else {
                newBranch.resolve(message -> {
                    switch (message) {
                        case "0":
                            this.resolvingBranchProcessMessage = "Cache new repository";
                            break;
                        case  "1":
                            this.resolvingBranchProcessMessage = "Cache new branch";
                            break;
                        case  "2":
                            this.resolvingBranchProcessMessage = "Update branch";
                            break;
                        case  "3":
                            this.resolvingBranchProcessMessage = "Cache huge repository, it may take some time, but only once";
                            break;
                    }
                }).subscribe(() -> {
                    if (user.tier1Backer) {
                        BranchState branchState = user.uiState.getBranchState();

                        if (branchState == null) {
                            branchState = new BranchState();
                            branchState.name = branchName;

                            user.uiState.addBranch(branchState);
                        }

                        final String[] expandedNodes = user.uiState.getBranchState().expandedNodes;
                        final String selectedNode = user.uiState.getBranchState().selectedNode;

                        if (expandedNodes != null) {
                            currentTreeState.expandedNodes.addAll(Arrays.asList(expandedNodes));
                        }

                        if (selectedNode != null) {
                            defaultSelectKey = selectedNode;
                        }
                    }

                    currentTreeState = treeStateByBranch.computeIfAbsent(branchName, key -> new TreeState());

                    harborState.currentBranch = branchName;

                    resolvingBranch = false;
                });
            }
        });
    }

    @JsMethod
    public void onNodeClick(RepositoryTreeNode repositoryTreeNode) {
        if (user.tier1Backer) {
            user.uiState.getBranchState().selectedNode = repositoryTreeNode.key;
        }

        if (repositoryTreeNode.leaf) {
            repository.getCurrentBranch()
                    .getFile(repositoryTreeNode.key)
                    .ifPresent(file -> sourceTabsSharedState.addSourceTab(file));
        }
    }

    @JsMethod
    public void onFavoriteClick() {
        repositoryTreeSharedState.inFavorites = repository.setFavorite();
    }

    @JsMethod
    public void onFromSource() {
        final SourceTabsState currentState = sourceTabsSharedState.getCurrentState();

        if (currentState != null && currentState.activeCodeTab != null) {
            revealKey(currentState.activeCodeTab);
        }
    }

    @JsMethod
    public void onContextMenu(MouseEvent mouseEvent, RepositoryTreeNode node) {
        treeComponent.setCurrentKey(node.key);

        contextMenuActions[1].visible = node.leaf;
        contextMenuActions[2].visible = !node.leaf;

        contextMenuPosition = new Position(mouseEvent.clientX, mouseEvent.clientY);
    }

    public void copyTreeNodeNameToClipBoard() {
        Copy.copyNodeName(treeComponent.getCurrentNode());
    }

    public void copyFileUrlToClipBoard() {
        Copy.copyNodeUrl(repository, treeComponent.getCurrentNode());
    }

    private void openCodeSearch() {
        harborState.window = CodeSearchWindow.create(treeComponent.getCurrentNode().key);
    }

    private void openHistory() {
        if (treeComponent.getCurrentNode().leaf) {
            harborState.window = FileHistoryWindow.create(treeComponent.getCurrentNode().key);

            return;
        }

        harborState.window = DirectoryHistoryWindow.create(treeComponent.getCurrentNode().key);
    }

    @Override
    public void destroyed() {
        repositoryTreeComponentApi.setApi(null);
        repositoryTreeComponentApi = null;
    }
}
