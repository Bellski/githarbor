package ru.githarbor.frontend.hello.vue.searchrepository;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.github.request.RepositorySearchRequest;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;
import ru.githarbor.frontend.vue.component.openrepository.OpenRepositoryComponent;
import ru.githarbor.frontend.vue.component.searchrepository.RepositoryListComponent;

import javax.inject.Inject;

import static elemental2.dom.DomGlobal.clearInterval;
import static elemental2.dom.DomGlobal.setTimeout;

@Component(components = {
        LoaderComponent.class,
        OpenRepositoryComponent.class,
        RepositoryListComponent.class
})
public class SearchRepositoryComponent implements IsVueComponent {

    @Inject
    public RepositorySearchRequest repositorySearchRequest;

    @Data
    public String input;

    @Data
    public boolean searching;

    @Data
    public RepositorySearchRequest.Repository[] repositories  = new RepositorySearchRequest.Repository[0];

    @Data
    public String toOpenRepository;

    private double inputInterval;

    @Watch("input")
    public void watchInput(String newInput) {
        clearInterval(inputInterval);

        if (input == null || input.isEmpty()) {
            repositories = new RepositorySearchRequest.Repository[0];
            return;
        }

        inputInterval = setTimeout(p0 -> {
            final Double innerInterval = Double.valueOf(inputInterval);

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
}
