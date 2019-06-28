package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.sourceview;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.dom.HTMLElement;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.jslib.monaco.IRange;
import ru.githarbor.frontend.harbor.jslib.monaco.MonacoFactory;
import ru.githarbor.frontend.harbor.jslib.monaco.Position;
import ru.githarbor.frontend.harbor.jslib.monaco.editor.IEditor;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.data.SourceTab;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;
import ru.githarbor.shared.User;

import javax.inject.Inject;

@Component(components = LoaderComponent.class)
public class SourceViewComponent implements IsVueComponent, HasCreated, HasMounted, HasBeforeDestroy {

    @Inject
    public User user;

    @Inject
    public Repository repository;

    @Inject
    public MonacoFactory monacoFactory;

    @Ref
    public HTMLElement monacoContainer;

    @Prop
    public SourceTab source;

    @Data
    public boolean loading = true;

    private IEditor monaco;

    @Override
    public void created() {


    }

    @Override
    public void mounted() {
        if (monacoFactory.isReady()) {
            init();

            return;
        }

        monacoFactory.onReady().subscribe(this::init);
    }

    private void init() {
        repository.getCurrentBranch()
                .getFile(source.key)
                .ifPresent(file -> monacoFactory.initModel(file).subscribe(iTextModel -> {
                    loading = false;

                    vue().$nextTick(() -> {
                        monaco = monacoFactory.create(monacoContainer);
                        monaco.setModel(iTextModel);

                        vue().$nextTick(() -> {
                            if (source.range != null) {
                                revealRange(source.range);
                            }

                            monaco.layout();
                            monaco.focus();
                        });
                    });
                }));
    }

    private void revealRange(IRange iRange) {
        monaco.revealLineInCenter(iRange.getStartLineNumber());
        monaco.setPosition(Position.create(iRange.getStartLineNumber(), iRange.getStartColumn()));

        monaco.focus();
    }

    @Override
    public void beforeDestroy() {
        if (monaco != null) {
            monaco.getModel().dispose();
            monaco.dispose();
            monaco = null;
            user = null;
            repository = null;
            monacoFactory = null;
        }
    }
}
