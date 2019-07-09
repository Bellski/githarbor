package ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.dirtree;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;
import io.reactivex.Observable;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.github.request.CommitFilesRequest;
import ru.githarbor.frontend.harbor.core.github.request.CommitsRequest;
import ru.githarbor.frontend.harbor.core.github.request.DiffContentRequest;
import ru.githarbor.frontend.harbor.jslib.MyKeyboardEvent;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.diff.DiffData;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.diff.SourceDiffComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.dirtree.data.FileNode;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.dirtree.data.Node;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.dirtree.data.RootNode;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static elemental2.dom.DomGlobal.setTimeout;

@Component(components = {
        LoaderComponent.class,
        SourceDiffComponent.class
})
public class DirectoryHistoryTreeComponent implements IsVueComponent, HasCreated {

    @Inject
    public Repository repository;

    @Inject
    public DiffContentRequest diffContentRequest;

    @Inject
    public CommitFilesRequest commitFilesRequest;

    @Prop
    public String directory;

    @Prop
    public CommitsRequest.Node commit;

    @Data
    public JsArray<Node> commitFiles;

    @Data
    public boolean loading = true;

    @Data
    public boolean loadingDiff = true;

    @Data
    public boolean visibleDiff = false;

    @Data
    public DiffData diffData;

    @Data
    public FileNode commitFile;

    @Ref
    public IsVueComponent diffDialog;

    @Ref
    public IsVueComponent treeComponent;

    private JsPropertyMap<JsArray<Node>> commitFilesCache = Js.cast(JsPropertyMap.of());

    private EventListener keyDownListener;

    @Override
    public void created() {
        vue().$watch(() -> visibleDiff, (newVisible, oldVisible) -> {
            if (newVisible) {
                vue().$nextTick(() -> {
                    setTimeout(p0 -> diffDialog.vue().$el().focus(), 100);

                    diffDialog.vue().$el().setAttribute("tabindex", 0);
                    diffDialog.vue().$el().addEventListener("keydown", keyDownListener = evt -> {
                        final MyKeyboardEvent myKeyboardEvent = Js.cast(evt);

                        if (myKeyboardEvent.getKeyCode() == 27) {
                            if (Arrays.asList(evt.path).contains(diffDialog.vue().$el())) {
                                onDiffClose();
                            }
                        }
                    });

                });

                return;
            }

            diffDialog.vue().$el().removeEventListener("keydown", keyDownListener);
        });

        vue().$watch(() -> commit, (newValue, oldValue) -> {
            loading = true;

            if (commitFilesCache.has(newValue.oid)) {
                commitFiles = commitFilesCache.get(newValue.oid);

                loading = false;

                return;
            }


            commitFilesRequest.execute(newValue.oid)
                    .flatMap(commitFiles -> Observable.fromArray(commitFiles)
                            .filter(commitFile -> commitFile.filename.startsWith(directory + "/"))
                            .toList())
                    .subscribe(commitFiles -> {
                        final Map<String, RootNode> rootNodeMap = new HashMap<>();

                        for (CommitFilesRequest.CommitFile commitFile : commitFiles) {
                            final RootNode rootNode = rootNodeMap.computeIfAbsent(commitFile.status, RootNode::new);

                            rootNode.children.push(new FileNode(commitFile));
                        }

                        final JsArray<Node> commitFilesTree = new JsArray<>();

                        rootNodeMap.values().forEach(commitFilesTree::push);

                        rootNodeMap.clear();

                        commitFilesCache.set(newValue.oid, commitFilesTree);

                        this.commitFiles = commitFilesTree;

                        loading = false;
                    });
        });
    }

    @JsMethod
    public void onCommitFileDoubleClick(Node fileNode) {
        commitFile = ((FileNode) fileNode);

        loadingDiff = true;
        visibleDiff = true;

        diffContentRequest.execute(commit.oid, ((FileNode) fileNode).fullPath)
                .subscribe(data -> {
                    final DiffContentRequest.BlobData originalContent = data.originalContent;
                    final DiffContentRequest.BlobData modifiedContent = data.modifiedContent;

                    if (modifiedContent == null) {
                        diffData = new DiffData(((FileNode) fileNode).name, null, originalContent.text);

                        loadingDiff = false;

                        return;
                    }

                    if (originalContent.oid.equals(modifiedContent.oid)) {
                        diffData = new DiffData(((FileNode) fileNode).name, null, originalContent.text);

                        loadingDiff = false;

                        return;
                    }

                    diffData = new DiffData(((FileNode) fileNode).name, modifiedContent.text, originalContent.text);

                    loadingDiff = false;
                });

    }

    @JsMethod
    public void onDiffClose() {
        visibleDiff = false;
        diffData = null;

        diffDialog.vue().$el().removeEventListener("keydown", keyDownListener);

        vue().$nextTick(() -> setTimeout(p0 -> {
            treeComponent.vue().$el().setAttribute("tabindex", 0);
            treeComponent.vue().$el().focus();
        }, 100));
    }

    @JsMethod
    public void onDiffOpen() {
        DomGlobal.console.warn("onDiffOpen");
    }
}
