package ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.imageview;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.vue.harbor.sourcetabs.data.SourceTab;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

import javax.inject.Inject;

@Component(components = {
        LoaderComponent.class
})
public class ImageViewComponent implements IsVueComponent {

    @Data
    public boolean loading = true;

    @Inject
    public Repository repository;

    @Prop
    public SourceTab source;

    @Computed
    public String getSrc() {
        return repository + "/" + repository.getCurrentBranch() + "/" + source.key;
    }

    @JsMethod
    public void handleOnLoadImage() {
        loading = false;
    }
}
