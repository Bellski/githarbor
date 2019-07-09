package ru.githarbor.frontend.harbor.vue.harbor;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasDestroyed;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLLinkElement;
import jsinterop.base.Js;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.monaco.Monaco;
import ru.githarbor.frontend.harbor.core.InitParameters;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.rpc.UserManagerRpcClient;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.jslib.HarborGlobal;
import ru.githarbor.frontend.harbor.jslib.MyKeyboardEvent;
import ru.githarbor.frontend.harbor.jslib.monaco.MonacoFactory;
import ru.githarbor.frontend.harbor.vue.harbor.reposearch.RepositorySearchComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.SourceTabsSharedState;
import ru.githarbor.frontend.harbor.vue.harbor.window.Window;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch2.CodeSearchComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch2.CodeSearchWindow;
import ru.githarbor.frontend.harbor.vue.harbor.window.filesearch.FileSearchComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.DirectoryHistoryComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.file.FileHistoryComponent;
import ru.githarbor.shared.User;

import javax.inject.Inject;

import static elemental2.dom.DomGlobal.*;
import static ru.githarbor.frontend.harbor.event.Events.WINDOW_RESIZED;

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
    MonacoFactory monacoFactory;

    @Inject
    public SourceTabsSharedState sourceTabsSharedState;

    @Inject
    public InitParameters initParameters;

    @Inject
    public User user;

    @Inject
    public UserManagerRpcClient userManagerRpcClient;

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

    @Data
    public boolean darkTheme;

    private double timeAgoInterval;

    private EventListener windowResizeListener;

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

    @Computed
    public boolean getHasDark() {
        return user.tier2Backer || user.tier1Backer;
    }

    @Override
    public void created() {
        darkTheme = user.darkTheme;

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

            vue().$emit("global-keydown", keyboardEvent);

            if (!keyboardEvent.ctrlKey) {
                if (keyboardEvent.altKey && keyCode == 78) { // alt + n
                    evt.preventDefault();

                    harborState.window = new Window(FileSearchComponent.NAME);

                    return;
                }

                if (keyboardEvent.altKey && keyCode == 70) { // alt+f
                    evt.preventDefault();

                    harborState.window = new CodeSearchWindow(null);

                    return;
                }
            }

            if (keyboardEvent.ctrlKey && keyboardEvent.shiftKey && keyboardEvent.getKeyCode() == 67) {
                evt.preventDefault();
            }
        });

        vue().$watch(() -> darkTheme, (newDarkTheme, oldDarkTheme) -> {
            final String themeName = newDarkTheme ? "dark" : "default";
            final String reverseThemeName = newDarkTheme ? "default" : "dark";

            final HTMLLinkElement linkElement = Js.cast(document.createElement("link"));
            linkElement.dataset.set(themeName, "");
            linkElement.rel = "stylesheet";
            linkElement.href = "/harbor/assets/webpack/css/" + (newDarkTheme ? "dark.harbor.css" : "default.harbor.css");
            linkElement.onload = p0 -> {
                document.head.querySelector("[data-" + reverseThemeName + "]").remove();

                return null;
            };

            document.head.appendChild(linkElement);

            userManagerRpcClient.setTheme(newDarkTheme);

            user.darkTheme = newDarkTheme;

            Monaco.setTheme(newDarkTheme ? "dark" : "light");
        });

        if (initParameters.blob) {
            repository.getCurrentBranch().getFile(initParameters.path).ifPresent(file -> {
                sourceTabsSharedState.addSourceTab(file, initParameters.selection);
            });
        }

        window.addEventListener("resize", windowResizeListener = evt -> vue().$emit(WINDOW_RESIZED, evt));
    }

    @Override
    public void destroyed() {
        DomGlobal.clearInterval(timeAgoInterval);
    }
}
