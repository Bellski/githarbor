package ru.githarbor.frontend.harbor.vue.harbor.window.history.commits;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.PropDefault;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.harbor.core.github.request.CommitsRequest;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

@Component(components = LoaderComponent.class)
public class CommitsListComponent implements IsVueComponent, HasCreated {

    @Prop
    public CommitsRequest.Node[] commits;

    @Prop
    public boolean loadMore;

    @Prop
    public boolean loadingMore;

    @Data
    public double commitIndex = 0;

    @PropDefault("loadMore")
    public boolean defaultLoadMore() {
        return false;
    }

    @PropDefault("loadingMore")
    public boolean defaultLoadingMore() {
        return false;
    }

    @JsMethod
    public void onCommitClick(double index, CommitsRequest.Node commit) {
        commitIndex = index;

        vue().$emit("select", commit);
    }

    @JsMethod
    public void onLoadMore() {
        vue().$emit("load-more");
    }

    @Override
    public void created() {
        vue().$watch(() -> commits.length, (newValue, oldValue) -> {
            if (oldValue == 0) {
                vue().$emit("select", commits[0]);
            }
        });
    }
}
