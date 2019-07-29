package ru.githarbor.frontend.harbor.vue.harbor.window.history.file;

import com.axellience.vuegwt.core.annotations.component.*;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.core.JsArray;
import elemental2.dom.Element;
import elemental2.dom.EventListener;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.github.request.CommitsRequest;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.jslib.MyKeyboardEvent;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.commits.CommitsListComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.file.diff.FileHistoryDiffComponent;

import javax.inject.Inject;
import java.util.Arrays;

import static elemental2.dom.DomGlobal.setTimeout;
import static elemental2.dom.DomGlobal.window;

@Component(components = {
        CommitsListComponent.class,
        FileHistoryDiffComponent.class
})
public class FileHistoryComponent implements IsVueComponent, HasCreated, HasMounted, HasBeforeDestroy {

    @Inject
    public HarborState harborState;

    @Inject
    public CommitsRequest commitsRequest;

    @Inject
    public Repository repository;

    @Data
    public String file;

    @Data
    public double listPane = 25;

    @Data
    public double diffPane = 75;

    @Data
    public JsArray<CommitsRequest.Node> allCommits = new JsArray<>();

    @Data
    public CommitsRequest.Node commit;

    @Ref
    public FileHistoryDiffComponent fileHistoryDiffComponent;


    @Data
    public CommitsRequest.PageInfo pageInfo;

    @Data
    public boolean loadingMore = false;

    private EventListener windowResizeListener;

    private EventListener keyDownListener;

    private Element elBodyElement;

    private boolean destroying = false;

    @Computed
    public boolean getLoadMore() {
        return pageInfo != null && pageInfo.hasNextPage;
    }

    @Computed
    public CommitsRequest.Node[] getCommits() {
        return Js.uncheckedCast(allCommits.slice(0));
    }

    @Override
    public void created() {
        file = Js.cast(harborState.window.props.get("file"));
    }

    @JsMethod
    public void onClose() {
        destroying = true;

        harborState.window = null;
    }

    @JsMethod
    public void onCommitSelect(CommitsRequest.Node commit) {
        this.commit = commit;
    }

    @JsMethod
    public void onLoadMore() {
        loadingMore = true;

        commitsRequest.execute(file, pageInfo.endCursor).subscribe(history -> {
            pageInfo = history.pageInfo;

            final CommitsRequest.Node[] commits = Arrays.stream(history.edges)
                    .map(edge -> edge.node)
                    .toArray(CommitsRequest.Node[]::new);

            allCommits.push(commits);

            loadingMore = false;
        });
    }

    @Override
    public void mounted() {
        window.addEventListener("resize", windowResizeListener = evt -> {
           if (fileHistoryDiffComponent != null) {
               fileHistoryDiffComponent.layout();
           }
        });

        vue().$nextTick(() -> {
            elBodyElement = vue().$el().querySelector(".el-dialog__body");

            setTimeout(p0 -> elBodyElement.focus(), 100);

            elBodyElement.setAttribute("tabindex", 0);
            elBodyElement.addEventListener("keydown", keyDownListener = evt -> {
                final MyKeyboardEvent myKeyboardEvent = Js.cast(evt);

                if (myKeyboardEvent.getKeyCode() == 27) {
                    onClose();
                }
            });

        });

        commitsRequest.execute(file).subscribe(history -> {

            if (!destroying) {
                pageInfo = history.pageInfo;

                CommitsRequest.Node[] commits = Arrays.stream(history.edges)
                        .map(edge -> edge.node)
                        .toArray(CommitsRequest.Node[]::new);

                allCommits.push(commits);
            }
        });
    }

    @JsMethod
    public void onResized(JsArray<JsPropertyMap> evt) {
        listPane = Js.cast(evt.getAt(0).get("width"));
        diffPane = Js.cast(evt.getAt(1).get("width"));

        if (fileHistoryDiffComponent != null) {
            fileHistoryDiffComponent.layout();
        }
    }

    @Override
    public void beforeDestroy() {
        destroying = true;

        window.removeEventListener("resize", windowResizeListener);
        elBodyElement.removeEventListener("keydown", keyDownListener);

        elBodyElement = null;
    }
}
