package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.sourceview;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.dom.HTMLElement;
import ru.githarbor.frontend.monaco.Disposable;
import ru.githarbor.frontend.monaco.action.CopyUrlSelectionAction;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.jslib.monaco.MonacoFactory;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.MonacoSourceTabComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.SourceTabsSharedState;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;
import ru.githarbor.shared.User;

import javax.inject.Inject;

@Component(components = LoaderComponent.class)
public class SourceViewComponent extends MonacoSourceTabComponent implements HasCreated, HasMounted, HasBeforeDestroy {

    @Inject
    public User user;

    @Inject
    public SourceTabsSharedState sourceTabsSharedState;

    @Inject
    public Repository repository;

    @Inject
    public MonacoFactory monacoFactory;

    @Ref
    public HTMLElement monacoContainer;

    @Data
    public boolean loading = true;

    @Data
    public String error;

    private Disposable copyActionDisposable;

    @Override
    public void created() {
        vue().$watch(() -> sourceTabsSharedState.getCurrentState().activeCodeTab, (newTab, oldTab) -> {
            if (loading) {
                mounted();
            }
        });
    }

    @Override
    public void mounted() {
        if (source.key.equals(sourceTabsSharedState.getCurrentState().activeCodeTab)) {
            init();
        }
    }

    private void init() {
        final File file = repository.getCurrentBranch().getFile(source.key).get();

        file.resolveContent().subscribe(content -> {
            loading = false;

            vue().$nextTick(() -> {
                monaco = monacoFactory.create(monacoContainer);
                copyActionDisposable = monaco.addAction(new CopyUrlSelectionAction(
                        repository.toString(),
                        repository.getCurrentBranch().name,
                        source.key
                ));
                monaco.setModel(monacoFactory.initModel(file.name, content));

                onMonacoCreated();

                vue().$nextTick(() -> {
                    if (source.range != null) {
                        revealRange(source.range);
                    }

                    monaco.layout();
                    monaco.focus();
                });
            });
        }, throwable -> {
            error = throwable.getMessage();
            loading = false;
        });
    }

    @Override
    public void beforeDestroy() {
        if (copyActionDisposable != null) {
            copyActionDisposable.dispose();
            copyActionDisposable = null;
        }

        user = null;
        repository = null;
        monacoFactory = null;
    }
}
