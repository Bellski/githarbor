package ru.githarbor.frontend.hello.vue.searchrepository;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import ru.githarbor.frontend.github.request.RepositorySearchRequest;
import ru.githarbor.frontend.hello.vue.HarborGlobal;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;
import ru.githarbor.frontend.vue.component.searchrepository.Repository;
import ru.githarbor.frontend.vue.component.searchrepository.RepositoryListComponent;

import javax.inject.Inject;

import java.util.Arrays;

import static elemental2.dom.DomGlobal.clearInterval;
import static elemental2.dom.DomGlobal.setTimeout;

@Component(components = {
        LoaderComponent.class,
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
    public ru.githarbor.frontend.vue.component.searchrepository.Repository[] repositories = new Repository[0];

    private double inputInterval;

    @Watch("input")
    public void watchInput(String newInput) {
        clearInterval(inputInterval);

        if (input == null || input.isEmpty()) {
            repositories = new Repository[0];
            return;
        }

        inputInterval = setTimeout(p0 -> {
            final Double innerInterval = Double.valueOf(inputInterval);

            searching = true;

            repositorySearchRequest.execute(newInput)
                    .subscribe(search -> {

                        if (innerInterval.equals(inputInterval)) {
                            repositories = Arrays.stream(search.repositories)
                                    .map(repository -> {
                                        final RepositorySearchRequest.PrimaryLanguage primaryLanguage = repository.primaryLanguage;

                                        String languageName = primaryLanguage != null ? primaryLanguage.name : null;
                                        String languageColor = primaryLanguage != null ? primaryLanguage.color : null;

                                        return new ru.githarbor.frontend.vue.component.searchrepository.Repository(
                                                repository.nameWithOwner,
                                                languageColor,
                                                languageName,
                                                HarborGlobal.kFormat(repository.stars),
                                                HarborGlobal.timeAgo(repository.updatedAt, "en")
                                        );
                                    })
                                    .toArray(ru.githarbor.frontend.vue.component.searchrepository.Repository[]::new);

                            searching = false;
                        }
                    });
        }, 300);
    }
}
