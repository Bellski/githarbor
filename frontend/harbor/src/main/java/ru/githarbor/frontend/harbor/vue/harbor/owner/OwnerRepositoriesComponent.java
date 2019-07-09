package ru.githarbor.frontend.harbor.vue.harbor.owner;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import ru.githarbor.frontend.github.request.RepositorySearchRequest;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.frontend.harbor.core.github.request.OwnerRepositoriesRequest;
import ru.githarbor.frontend.harbor.jslib.HarborGlobal;
import ru.githarbor.frontend.harbor.vue.harbor.reposearch.OpenRepositoryComponent;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;
import ru.githarbor.frontend.vue.component.searchrepository.RepositoryListComponent;

import javax.inject.Inject;
import java.util.Arrays;

@Component(components = {
        LoaderComponent.class,
        OpenRepositoryComponent.class,
        RepositoryListComponent.class
})
public class OwnerRepositoriesComponent implements IsVueComponent, HasCreated {

    @Inject
    public OwnerRepositoriesRequest ownerRepositoriesRequest;

    @Inject
    public Repository repository;

    @Data
    public String input;

    @Data
    public boolean loading = true;

    @Data
    public ru.githarbor.frontend.vue.component.searchrepository.Repository[] repositories;

    private ru.githarbor.frontend.vue.component.searchrepository.Repository[] allRepositories;

    @Watch("input")
    public void watchInput(String newInput) {
        if (newInput.isEmpty()) {
            repositories = allRepositories;

            return;
        }

        repositories = Arrays.stream(allRepositories).filter(repository -> repository.nameWithOwner.contains(newInput))
                .toArray(ru.githarbor.frontend.vue.component.searchrepository.Repository[]::new);
    }

    @Override
    public void created() {
        ownerRepositoriesRequest.execute(repository.info.nameWithOwner.owner)
                .subscribe(repositories -> {
                    allRepositories = Arrays.stream(repositories)
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

                    this.repositories = allRepositories;

                    loading = false;
                });
    }
}
