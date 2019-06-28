package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import elemental2.core.JsArray;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.data.SourceTab;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.readme.ReadMeComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.sourceview.SourceViewComponent;

import javax.inject.Inject;

@Component(components = {
        SourceViewComponent.class,
        ReadMeComponent.class
})
public class SourceTabsComponent implements IsVueComponent, HasCreated {

    @Inject
    public Repository repository;

    @Inject
    public HarborState harborState;

    @Data
    @Inject
    public SourceTabsSharedState sharedState;

    @Data
    public boolean switchingBranch;

    @Computed
    public boolean showReadme() {
        return !sharedState.hasTabs();
    }

    @Computed
    public JsArray<SourceTab> getTabs() {
        return sharedState.getCurrentState().tabs;
    }

    @Computed
    public String getActiveTab() {
        return sharedState.getCurrentState().activeCodeTab;
    }

    @Computed
    public void setActiveTab(String activeTab) {
        sharedState.getCurrentState().activeCodeTab = activeTab;
    }

    @JsMethod
    public void onTabRemove(String key) {
        sharedState.removeSourceTab(key);
    }

    @Override
    public void created() {
        vue().$watch(() -> harborState.currentBranch, (newValue, oldValue) -> {
            switchingBranch = true;

            vue().$nextTick(() -> {
                sharedState.switchState(newValue);

                switchingBranch = false;
            });
        });
    }
}
