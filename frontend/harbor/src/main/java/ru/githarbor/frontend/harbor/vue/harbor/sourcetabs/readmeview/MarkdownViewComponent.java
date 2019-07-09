package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.readmeview;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import elemental2.dom.HTMLElement;
import ru.githarbor.frontend.harbor.core.github.core.File;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.jslib.monaco.MonacoFactory;
import ru.githarbor.frontend.harbor.vue.component.markdown.MarkDownComponent;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.MonacoSourceTabComponent;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

import javax.inject.Inject;

@Component(components = {
        LoaderComponent.class,
        MarkDownComponent.class
})
public class MarkdownViewComponent extends MonacoSourceTabComponent implements HasCreated, HasBeforeDestroy {

    @Inject
    public MonacoFactory monacoFactory;

    @Inject
    public Repository repository;

    @Data
    public boolean loading = true;

    @Data
    public String activeTab = "render";

    @Ref
    public HTMLElement monacoContainer;

    private File file;

    @Override
    public void created() {
        repository.getCurrentBranch().getFile(source.key).ifPresent(file -> {
            this.file = file;

            file.resolveContent().subscribe(content -> loading = false);
        });

        vue().$watch(() -> activeTab, (newTab, oldTab) -> {
            if (monaco == null) {
                monaco = monacoFactory.create(monacoContainer);
                monaco.setModel(monacoFactory.initModel(file.name, file.getContent()));

                vue().$nextTick(() -> monaco.layout());

                return;
            }

            vue().$nextTick(() -> monaco.layout());
        });
    }

    @Override
    public void beforeDestroy() {

    }
}
