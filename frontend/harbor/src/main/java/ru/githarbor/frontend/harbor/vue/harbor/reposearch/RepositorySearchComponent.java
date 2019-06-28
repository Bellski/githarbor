package ru.githarbor.frontend.harbor.vue.harbor.reposearch;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import elemental2.dom.DomGlobal;
import elemental2.dom.MouseEvent;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import ru.githarbor.frontend.github.request.RepositorySearchRequest;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;

import javax.inject.Inject;

import java.util.Arrays;

import static elemental2.dom.DomGlobal.clearInterval;
import static elemental2.dom.DomGlobal.setTimeout;

@Component(components = {LoaderComponent.class, OpenRepositoryComponent.class})
public class RepositorySearchComponent implements IsVueComponent, HasMounted {

    @Inject
    public RepositorySearchRequest repositorySearchRequest;

    @Data
    public String input;

    @Data
    public boolean searching;

    @Data
    public boolean visible;

    @Data
    public RepositorySearchRequest.Repository[] repositories  = new RepositorySearchRequest.Repository[0];

    @Data
    public String toOpenRepository;

    @Ref
    public IsVueComponent inputElement;

    private double inputInterval;

    private String popoverId;

    @Watch("input")
    public void watchInput(String newInput) {
        clearInterval(inputInterval);

        if (input == null || input.isEmpty()) {
            repositories = new RepositorySearchRequest.Repository[0];
            visible = false;

            return;
        }

        inputInterval = setTimeout(p0 -> {
            final Double innerInterval = Double.valueOf(inputInterval);

            visible = true;
            searching = true;

            repositorySearchRequest.execute(newInput)
                    .subscribe(search -> {

                        if (innerInterval.equals(inputInterval)) {
                            repositories = search.repositories;

                            searching = false;
                        }
                    });
        }, 300);
    }

    @JsMethod
    public void onRepositoryOpen(RepositorySearchRequest.Repository repository) {
        this.toOpenRepository = repository.nameWithOwner;
    }

    @Override
    public void mounted() {
        DomGlobal.window.addEventListener("click", evt -> {
            final MouseEvent mouseEvent = Js.cast(evt);

            if (!Arrays.asList(mouseEvent.path).contains(inputElement.vue().$el())) {
                visible = false;
                searching = false;
                repositories = new RepositorySearchRequest.Repository[0];
            }
        });

        inputElement.vue().$el().addEventListener("focusin", evt -> {
            if (!visible) {
                watchInput(input);
            }
        });
    }
}
