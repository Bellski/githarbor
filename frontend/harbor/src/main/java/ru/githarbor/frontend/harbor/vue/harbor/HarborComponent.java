package ru.githarbor.frontend.harbor.vue.harbor;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasDestroyed;
import elemental2.dom.DomGlobal;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.jslib.HarborGlobal;
import ru.githarbor.frontend.harbor.jslib.MyKeyboardEvent;
import ru.githarbor.frontend.harbor.vue.harbor.reposearch.RepositorySearchComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.Window;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch2.CodeSearchComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.filesearch.FileSearchComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.DirectoryHistoryComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.file.FileHistoryComponent;

import javax.inject.Inject;

import static elemental2.dom.DomGlobal.window;

@Component(components = {
        RepositoryInfoComponent.class,
        HarborSideBar.class,
        RepositorySearchComponent.class,
        FileSearchComponent.class,
        FileHistoryComponent.class,
        DirectoryHistoryComponent.class,
        CodeSearchComponent.class
})
public class HarborComponent implements IsVueComponent, HasCreated, HasDestroyed {

    @Inject
    public GitHubGqlClient gitHubGqlClient;

    @Inject
    public Repository repository;

    @Inject
    @Data
    public HarborState harborState;

    @Data
    public double queryLimitRemaining;

    @Data
    public String queryLimitResetAt;

    @Data
    public String latestCommit;


    private double timeAgoInterval;

    @Computed
    public String getOwnerWithName() {
        return repository.info.nameWithOwner.owner + " / " + repository.info.nameWithOwner.name;
    }

    @Computed
    public String getFormattedRateLimit() {
        return queryLimitRemaining +
                "/" +
                "5000"
                + " (reset "
                + queryLimitResetAt + ")";
    }

    @Computed
    public String getStars() {
        return repository.info.stars;
    }

    @Computed
    public String getWindow() {
        return harborState.window != null ? harborState.window.name : null;
    }

    @Computed
    public Object getWindowProps() {
        return harborState.window != null ? harborState.window.props : null;
    }

    @Override
    public void created() {

        queryLimitResetAt = HarborGlobal.timeAgo(gitHubGqlClient.getRateLimit().resetAt);
        latestCommit = HarborGlobal.timeAgo(repository.getCurrentBranch().committedDate);

        gitHubGqlClient.onRateLimitUpdate()
                .subscribe(rateLimit -> {
                    queryLimitRemaining = rateLimit.remaining;
                    queryLimitResetAt = HarborGlobal.timeAgo(rateLimit.resetAt);
                });

        timeAgoInterval = DomGlobal.setInterval(p0 -> {
            queryLimitResetAt = HarborGlobal.timeAgo(gitHubGqlClient.getRateLimit().resetAt);
            latestCommit = HarborGlobal.timeAgo(repository.getCurrentBranch().committedDate);

        }, 40000);


        window.addEventListener("keydown", evt -> {
            final MyKeyboardEvent keyboardEvent = (MyKeyboardEvent) evt;
            final int keyCode = keyboardEvent.getKeyCode();

            if (!keyboardEvent.ctrlKey) {
                if (keyboardEvent.altKey && keyCode == 78) { // alt + n
                    evt.preventDefault();

                    harborState.window = new Window(FileSearchComponent.NAME);
                }
            }
        });
    }

    @Override
    public void destroyed() {
        DomGlobal.clearInterval(timeAgoInterval);
    }
}
