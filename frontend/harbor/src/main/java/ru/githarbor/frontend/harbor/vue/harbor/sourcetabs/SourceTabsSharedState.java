package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs;

import elemental2.core.JsArray;
import jsinterop.annotations.JsProperty;
import jsinterop.base.Js;
import ru.githarbor.frontend.monaco.IRange;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.data.SourceTab;
import ru.githarbor.shared.FileState;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class SourceTabsSharedState {

    @JsProperty
    private final Map<String, SourceTabsState> tabsStateByBranchName = new HashMap<>();

    private SourceTabsState currentState;

    private User user;

    @Inject
    public SourceTabsSharedState(Repository repository, User user) {
        currentState = tabsStateByBranchName.computeIfAbsent(repository.getCurrentBranch().name, key -> new SourceTabsState());
        this.user = user;
    }

    public void switchState(String branchName) {
        currentState = tabsStateByBranchName.computeIfAbsent(branchName, key -> new SourceTabsState());
    }

    public SourceTabsState getCurrentState() {
        return currentState;
    }

    public boolean hasTabs() {
        return currentState.tabs.length > 0;
    }

    public void addSourceTab(File file) {
        addSourceTab(file, null);
    }

    public void addSourceTab(File file, IRange range) {
        SourceTab codeTab = currentState.tabs.find((p0, p1, p2) -> p0.key.equals(file.getPath()));

        if (codeTab == null) {
            currentState.tabs.push(codeTab = new SourceTab(file.name, file.getPath()));
        }

        codeTab.range = range;

        currentState.activeCodeTab = codeTab.key;

        updateUiState();
    }

    public void removeSourceTab(String key) {
        if (currentState.tabs.length > 1) {
            final int indexOfTab = currentState.tabs.indexOf(currentState.tabs.find((p0, p1, p2) -> p0.key.equals(key)));

            if (currentState.activeCodeTab.equals(key)) {
                currentState.activeCodeTab = currentState.tabs.getAt(indexOfTab > 0 ? indexOfTab - 1 : indexOfTab + 1).key;
            }
        }

        currentState.tabs = new JsArray<>(Js.uncheckedCast(currentState.tabs.filter((p0, p1, p2) -> !p0.key.equals(key))));

        updateUiState();
    }

    public void removeAll() {
        currentState.tabs = new JsArray<>();
        currentState.activeCodeTab = null;

        if (user.tier1Backer) {
            user.uiState.getBranchState().openedFiles = new FileState[0];
            user.uiState.getBranchState().activeOpenedFile = null;
        }
    }

    public void removeOthers(String except) {
        currentState.tabs = Js.uncheckedCast(currentState.tabs.filter((p0, p1, p2) -> p0.key.equals(except)));
        currentState.activeCodeTab = currentState.tabs.getAt(0).key;

        updateUiState();
    }

    private void updateUiState() {
        if (user.tier1Backer) {
            user.uiState.getBranchState().openedFiles = Arrays.stream(Js.<SourceTab[]>uncheckedCast(currentState.tabs.slice(0)))
                    .map(sourceTab -> {
                        final FileState fileState = new FileState();
                        fileState.name = sourceTab.key;

                        return fileState;
                    })
                    .toArray(FileState[]::new);

            user.uiState.getBranchState().activeOpenedFile = currentState.activeCodeTab;
        }
    }
}
