package ru.githarbor.frontend.harbor.vue.harbor.window.history.diff;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import com.axellience.vuegwt.core.client.component.options.functions.OnEvent;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import ru.githarbor.frontend.harbor.event.Events;
import ru.githarbor.frontend.harbor.jslib.monaco.MonacoFactory;
import ru.githarbor.frontend.monaco.editor.IEditor;
import ru.githarbor.frontend.monaco.editor.StandaloneDiffEditor;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

import javax.inject.Inject;

@Component(components = {
        LoaderComponent.class
})
public class SourceDiffComponent implements IsVueComponent, HasMounted, HasCreated, HasBeforeDestroy {

    @Inject
    public MonacoFactory monacoFactory;

    @Prop
    public DiffData data;

    public HTMLElement diffContainer;

    public IEditor monaco;

    private OnEvent onWindowResized;

    @Override
    public void created() {
        vue().$root().vue().$on(Events.WINDOW_RESIZED, onWindowResized = parameter -> {
            if (monaco != null) {
                monaco.layout();
            }
        });
    }

    @Override
    public void mounted() {
        processDiff(data);
    }

    private void processDiff(DiffData data) {

        if (data.modified == null) {
            createContainerElement();

            monaco = monacoFactory.create(diffContainer);
            monaco.setModel(monacoFactory.initModel(data.fileName, data.original));

        } else {
            createContainerElement();

            monaco = monacoFactory.createDiff(diffContainer, data.modified, data.original, data.fileName);
        }

        monaco.layout();
    }

    private void dispose() {
        if (monaco != null) {
            if (monaco.isDiff()) {

                ((StandaloneDiffEditor) monaco).getOriginalEditor().dispose();
                ((StandaloneDiffEditor) monaco).getModifiedEditor().dispose();

                monaco.dispose();

                return;
            }

            monaco.getModel().dispose();
            monaco.dispose();
        }

        diffContainer.remove();
        diffContainer = null;
    }

    private void createContainerElement() {
        diffContainer = Js.cast(DomGlobal.document.createElement("div"));
        diffContainer.style.width = CSSProperties.WidthUnionType.of("100%");
        diffContainer.style.height = CSSProperties.HeightUnionType.of("100%");

        vue().$el().appendChild(diffContainer);
    }

    @Override
    public void beforeDestroy() {
        if (monaco != null) {
            vue().$root().vue().$off(Events.WINDOW_RESIZED, onWindowResized);

            dispose();

            monaco = null;
        }
    }

    public void layout() {
        if (monaco != null) {
            monaco.layout();
        }
    }
}

