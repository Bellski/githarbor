package ru.githarbor.frontend.harbor.vue.harbor.window.history.dir;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasDestroyed;
import elemental2.core.JsArray;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.github.request.CommitsRequest;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.commits.CommitsListComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.dirtree.DirectoryHistoryTreeComponent;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

import javax.inject.Inject;
import java.util.Arrays;

@Component(components = {
        LoaderComponent.class,
        CommitsListComponent.class,
        DirectoryHistoryTreeComponent.class
})
public class DirectoryHistoryComponent implements IsVueComponent, HasCreated, HasDestroyed {

    @Inject
    public HarborState harborState;

    @Inject
    public CommitsRequest commitsRequest;

    @Inject
    public Repository repository;

    @Prop
    public String directory;

    @Data
    public double listPane = 25;

    @Data
    public double diffPane = 75;

    @Data
    public JsArray<CommitsRequest.Node> allCommits = new JsArray<>();

    @Data
    public CommitsRequest.Node commit;

    @Data
    public CommitsRequest.PageInfo pageInfo;

    @Data
    public boolean loadingMore = false;

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
        commitsRequest.execute(directory).subscribe(history -> {
            pageInfo = history.pageInfo;

            CommitsRequest.Node[] commits = Arrays.stream(history.edges)
                    .map(edge -> edge.node)
                    .toArray(CommitsRequest.Node[]::new);

            allCommits.push(commits);
        });
    }

    @Override
    public void destroyed() {

    }

    @JsMethod
    public void onClose() {
        harborState.window = null;
    }

    @JsMethod
    public void onCommitSelect(CommitsRequest.Node commit) {
        this.commit = commit;
    }

    @JsMethod
    public void onResized(JsArray<JsPropertyMap> evt) {
        listPane = Js.cast(evt.getAt(0).get("width"));
        diffPane = Js.cast(evt.getAt(1).get("width"));
    }

    @JsMethod
    public void onLoadMore() {
        loadingMore = true;

        commitsRequest.execute(directory, pageInfo.endCursor).subscribe(history -> {
            pageInfo = history.pageInfo;

            final CommitsRequest.Node[] commits = Arrays.stream(history.edges)
                    .map(edge -> edge.node)
                    .toArray(CommitsRequest.Node[]::new);

            allCommits.push(commits);

            loadingMore = false;
        });
    }
}
