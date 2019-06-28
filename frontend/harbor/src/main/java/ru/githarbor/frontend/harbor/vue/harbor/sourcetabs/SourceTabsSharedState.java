package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs;

import elemental2.core.JsArray;
import jsinterop.annotations.JsProperty;
import jsinterop.base.Js;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.jslib.monaco.IRange;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.data.SourceTab;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class SourceTabsSharedState {

    @JsProperty
    private final Map<String, SourceTabsState> tabsStateByBranchName = new HashMap<>();

    private SourceTabsState currentState;

    @Inject
    public SourceTabsSharedState(Repository repository) {
        currentState = tabsStateByBranchName.computeIfAbsent(repository.getCurrentBranch().name, key -> new SourceTabsState());
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
    }

    public void removeSourceTab(String key) {
        if (currentState.tabs.length > 1) {
            final int indexOfTab = currentState.tabs.indexOf(currentState.tabs.find((p0, p1, p2) -> p0.key.equals(key)));

            if (currentState.activeCodeTab.equals(key)) {
                currentState.activeCodeTab = currentState.tabs.getAt(indexOfTab > 0 ? indexOfTab -1 : indexOfTab + 1).key;
            }
        }

        currentState.tabs = new JsArray<>(Js.uncheckedCast(currentState.tabs.filter((p0, p1, p2) -> !p0.key.equals(key))));
    }
}
