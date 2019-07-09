package ru.githarbor.frontend.harbor.vue.component.monaco;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.options.functions.OnEvent;
import ru.githarbor.frontend.monaco.IRange;
import ru.githarbor.frontend.monaco.Position;
import ru.githarbor.frontend.harbor.event.Events;
import ru.githarbor.frontend.monaco.editor.IEditor;

@Component(hasTemplate = false)
public class MonacoContainerComponent implements IsVueComponent, HasCreated, HasBeforeDestroy {

    protected IEditor monaco;

    private OnEvent onWindowResized;

    private OnEvent onMainSideBarResized;


    @Override
    public void created() {
        vue().$root().vue().$on(Events.WINDOW_RESIZED, onWindowResized = parameter -> {
            if (monaco != null) {
                monaco.layout();
            }
        });

        vue().$root().vue().$on(Events.MAIN_SIDEBAR_RESIZED, onMainSideBarResized = parameter -> {
            if (monaco != null) {
                monaco.layout();
            }
        });
    }

    protected void revealRange(IRange iRange) {
        monaco.revealLineInCenter(iRange.getStartLineNumber());

        monaco.setPosition(Position.create(iRange.getStartLineNumber(), iRange.getStartColumn()));
        monaco.setSelection(iRange);

        monaco.focus();
    }

    @Override
    public void beforeDestroy() {
        vue().$root().vue().$off(Events.WINDOW_RESIZED, onWindowResized);
        vue().$root().vue().$off(Events.MAIN_SIDEBAR_RESIZED, onMainSideBarResized);

        if (monaco != null) {
            monaco.getModel().dispose();
            monaco.dispose();
            monaco = null;
        }
    }
}
