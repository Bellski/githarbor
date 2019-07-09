package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import elemental2.core.JsArray;
import elemental2.dom.DomGlobal;
import elemental2.dom.MouseEvent;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.harbor.core.ImageType;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.state.HarborState;
import ru.githarbor.frontend.harbor.vue.component.menu.Action;
import ru.githarbor.frontend.harbor.vue.component.menu.ContextMenuComponent;
import ru.githarbor.frontend.harbor.vue.component.menu.Position;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.data.SourceTab;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.imageview.ImageViewComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.javaview.JavaSourceViewComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.readme.ReadmeComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.readmeview.MarkdownViewComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.sourceview.SourceViewComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.svgview.SvgViewComponent;
import ru.githarbor.frontend.harbor.vue.harbor.window.codesearch2.CodeSearchWindow;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.dir.DirectoryHistoryWindow;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.file.FileHistoryWindow;
import ru.githarbor.shared.BranchState;
import ru.githarbor.shared.FileState;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import java.util.Arrays;

@Component(components = {
        SourceViewComponent.class,
        ReadmeComponent.class,
        ImageViewComponent.class,
        SvgViewComponent.class,
        MarkdownViewComponent.class,
        JavaSourceViewComponent.class,
        ContextMenuComponent.class
})
public class SourceTabsComponent implements IsVueComponent, HasCreated {

    @Inject
    public User user;

    @Inject
    public Repository repository;

    @Inject
    public HarborState harborState;

    @Data
    @Inject
    public SourceTabsSharedState sharedState;

    @Data
    public boolean switchingBranch;

    @Data
    public Action[] contextMenuActions;

    @Data
    public Position contextMenuPosition;

    private SourceTab contextMenuSourceTab;

    @Computed
    public boolean getShowReadme() {
        return !sharedState.hasTabs();
    }

    @JsMethod
    public boolean isImage(SourceTab sourceTab) {
        return ImageType.isImage(File.extension(sourceTab.key));
    }

    @JsMethod
    public boolean isSvg(SourceTab sourceTab) {
        return ImageType.isSvg(File.extension(sourceTab.key));
    }

    @JsMethod
    public boolean isMarkdown(SourceTab sourceTab) {
        return File.extension(sourceTab.key).toLowerCase().equals("md");
    }

    @JsMethod
    public boolean isJava(SourceTab sourceTab) {
        return File.extension(sourceTab.key).toLowerCase().equals("java");
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
        if (user.tier1Backer) {
            final BranchState branchState = user.uiState.getBranchState();

            sharedState.getCurrentState().activeCodeTab = branchState.activeOpenedFile;

            final FileState[] openedFiles = branchState.openedFiles;

            if (openedFiles != null) {
                for (FileState openedFile : openedFiles) {
                    final File file = repository.getCurrentBranch().getFile(openedFile.name).get();
                    sharedState.addSourceTab(file);
                }
            }
        }

        contextMenuActions = new Action[] {
                new Action("Close", () -> {
                    sharedState.removeSourceTab(contextMenuSourceTab.key);
                }),
                new Action("Close others", () -> {
                    sharedState.removeOthers(contextMenuSourceTab.key);
                }),
                new Action("Close All", () -> {
                    sharedState.removeAll();
                }),
        };

        vue().$watch(() -> harborState.currentBranch, (newValue, oldValue) -> {
            switchingBranch = true;

            vue().$nextTick(() -> {
                sharedState.switchState(newValue);

                if (user.tier1Backer) {
                    final BranchState branchState = user.uiState.getBranchState();

                    sharedState.getCurrentState().activeCodeTab = branchState.activeOpenedFile;

                    final FileState[] openedFiles = branchState.openedFiles;

                    if (openedFiles != null) {
                        for (FileState openedFile : openedFiles) {
                            final File file = repository.getCurrentBranch().getFile(openedFile.name).get();
                            sharedState.addSourceTab(file);
                        }
                    }
                }

                switchingBranch = false;
            });
        });
    }

    @JsMethod
    public void handleContextMenu(MouseEvent mouseEvent, SourceTab sourceTab) {
        mouseEvent.preventDefault();

        contextMenuSourceTab = sourceTab;

        contextMenuPosition = new Position(mouseEvent.clientX, mouseEvent.clientY);
    }
}
