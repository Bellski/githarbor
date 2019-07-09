package ru.githarbor.frontend.harbor.vue.harbor.window.history.file.diff;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasBeforeDestroy;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.google.gwt.core.client.JsDate;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.github.request.CommitsRequest;
import ru.githarbor.frontend.harbor.core.github.request.DiffContentRequest;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.diff.DiffData;
import ru.githarbor.frontend.harbor.vue.harbor.window.history.diff.SourceDiffComponent;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

import javax.inject.Inject;

@Component(components = {
        LoaderComponent.class,
        SourceDiffComponent.class
})
public class FileHistoryDiffComponent implements IsVueComponent, HasCreated, HasBeforeDestroy {

    @Inject
    public DiffContentRequest diffContentRequest;

    @Inject
    public Repository repository;

    @Prop
    public CommitsRequest.Node commit;

    @Prop
    public String file;

    @Data
    public DiffData diffData;

    @Data
    public boolean loadingDiff = true;

    @Ref
    public SourceDiffComponent sourceDiff;

    private JsPropertyMap<DiffContentRequest.Data> diffByCommit = Js.cast(JsPropertyMap.of());

    private double requestInterval;

    private boolean destroying;

    @Override
    public void created() {
        vue().$watch(() -> commit, (newValue, oldValue) -> {
            loadingDiff = true;

            requestInterval = JsDate.now();

            if (diffByCommit.has(newValue.oid)) {
                vue().$nextTick(() -> {
                    processDiff(diffByCommit.get(newValue.oid));
                });

                return;
            }

            diffContentRequest.execute(newValue.oid, file).subscribe(data -> {
                if (!destroying) {
                    diffByCommit.set(newValue.oid, data);

                    processDiff(data);
                }
            });
        });
    }

    private void processDiff(DiffContentRequest.Data data) {
        final DiffContentRequest.BlobData originalContent = data.originalContent;
        final DiffContentRequest.BlobData modifiedContent = data.modifiedContent;

        if (modifiedContent == null) {
            diffData = new DiffData(file, null, originalContent.text);

            loadingDiff = false;

            return;
        }

        if (originalContent.oid.equals(modifiedContent.oid)) {
            diffData = new DiffData(file, null, originalContent.text);

            loadingDiff = false;

            return;
        }

        diffData = new DiffData(file, modifiedContent.text, originalContent.text);

        loadingDiff = false;
    }


    public void layout() {
        if (sourceDiff != null) {
            sourceDiff.layout();
        }
    }

    @Override
    public void beforeDestroy() {
        destroying = true;
        diffByCommit = null;
    }
}
