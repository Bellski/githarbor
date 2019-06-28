package ru.githarbor.frontend.harbor.vue.harbor.window.history.diff;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import ru.githarbor.frontend.harbor.jslib.monaco.MonacoFactory;
import ru.githarbor.frontend.harbor.jslib.monaco.editor.IEditor;
import ru.githarbor.frontend.harbor.jslib.monaco.editor.StandaloneDiffEditor;
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

    @Data
    public boolean loadingMonaco = false;

    public HTMLElement diffContainer;

    public IEditor editor;

    @Override
    public void created() {
    }

    @Override
    public void mounted() {
        if (monacoFactory.isReady()) {
            processDiff(data);

            return;
        }

        loadingMonaco = true;

        monacoFactory.onReady().subscribe(() -> {
            loadingMonaco = false;

            vue().$nextTick(() -> processDiff(data));
        });
    }

    private void processDiff(DiffData data) {

        if (data.modified == null) {
            createContainerElement();

            editor = monacoFactory.create(diffContainer);
            editor.setModel(monacoFactory.initModel(data.fileName, data.original));

        } else {
            createContainerElement();

            editor = monacoFactory.createDiff(diffContainer, data.modified, data.original, data.fileName);
        }

        editor.layout();
    }

    private void dispose() {
        if (editor != null) {
            if (editor.isDiff()) {

                ((StandaloneDiffEditor) editor).getOriginalEditor().dispose();
                ((StandaloneDiffEditor) editor).getModifiedEditor().dispose();

                editor.dispose();

                return;
            }

            editor.getModel().dispose();
            editor.dispose();
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
        if (editor != null) {
            dispose();
            editor = null;
        }
    }
}

