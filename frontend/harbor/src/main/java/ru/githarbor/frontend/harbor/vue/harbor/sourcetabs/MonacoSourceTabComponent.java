package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import ru.githarbor.frontend.monaco.Disposable;
import ru.githarbor.frontend.monaco.IRange;
import ru.githarbor.frontend.harbor.vue.component.monaco.MonacoContainerComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.data.SourceTab;
import ru.githarbor.shared.User;

import javax.inject.Inject;

@Component(hasTemplate = false)
public class MonacoSourceTabComponent extends MonacoContainerComponent implements IsVueComponent, HasCreated, HasBeforeDestroy {

    @Inject
    public User user;

    @Inject
    public SourceTabsSharedState sourceTabsSharedState;

    @Prop
    public SourceTab source;

    private Disposable onDidChangeCursorPositionDisposable;
    private Disposable onDidChangeCursorSelectionDisposable;


    @Computed
    public boolean getIsDark() {
        return user.darkTheme;
    }

    @Override
    public void created() {
        vue().$watch(() -> sourceTabsSharedState.getCurrentState().activeCodeTab, (newTab, oldTab) -> {
            if (source.key.equals(newTab) && monaco != null) {
                monaco.layout();
            }
        });
    }

    protected void onMonacoCreated() {
        if (source.range != null) {
            revealRange(source.range);
        }

        onDidChangeCursorPositionDisposable = monaco.onDidChangeCursorPosition(event -> {
            source.range = IRange.create(event.position.lineNumber, event.position.lineNumber, event.position.column);
        });

        onDidChangeCursorSelectionDisposable =  monaco.onDidChangeCursorSelection(event -> {
            source.range = event.selection;
        });
    }

    @Override
    public void beforeDestroy() {
        if (monaco != null) {
            onDidChangeCursorPositionDisposable.dispose();
            onDidChangeCursorSelectionDisposable.dispose();
        }
    }
}
