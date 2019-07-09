package ru.githarbor.frontend.hello.vue.favorite;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import ru.githarbor.frontend.github.request.FavoriteRepositoriesRequest;
import ru.githarbor.frontend.github.request.RepositorySearchRequest;
import ru.githarbor.frontend.hello.rpc.UserManagerRpcClient;
import ru.githarbor.frontend.hello.vue.HarborGlobal;
import ru.githarbor.frontend.vue.component.loader.LoaderComponent;
import ru.githarbor.frontend.vue.component.searchrepository.Repository;
import ru.githarbor.frontend.vue.component.searchrepository.RepositoryListComponent;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import java.util.Arrays;

@Component(components = {
        LoaderComponent.class,
        RepositoryListComponent.class
})
public class FavoriteRepositoriesComponent implements IsVueComponent, HasCreated {

    @Inject
    public User user;

    @Inject
    public FavoriteRepositoriesRequest favoriteRepositoriesRequest;

    @Inject
    public UserManagerRpcClient userManagerRpcClient;

    @Data
    public boolean loading = true;

    @Data
    public String input;

    @Data
    public Repository[] repositories;

    public Repository[] allRepositories;

    @Watch("input")
    public void watchInput(String newInput) {
        if (newInput.isEmpty()) {
            repositories = allRepositories;

            return;
        }

        repositories = Arrays.stream(allRepositories).filter(repository -> repository.nameWithOwner.contains(newInput))
                .toArray(Repository[]::new);
    }

    @Override
    public void created() {
        if (user.favoriteRepositories != null) {
            favoriteRepositoriesRequest.execute(user.favoriteRepositories).subscribe(repositoryJsArray -> {
                allRepositories = Arrays.stream(Js.<FavoriteRepositoriesRequest.Repository[]>uncheckedCast(repositoryJsArray.slice(0)))
                        .map(repository -> {
                            final RepositorySearchRequest.PrimaryLanguage primaryLanguage = repository.primaryLanguage;

                            String languageName = primaryLanguage != null ? primaryLanguage.name : null;
                            String languageColor = primaryLanguage != null ? primaryLanguage.color : null;

                            return new Repository(
                                    repository.nameWithOwner,
                                    languageColor,
                                    languageName,
                                    HarborGlobal.kFormat(repository.stars),
                                    HarborGlobal.timeAgo(repository.updatedAt, "en")
                            );
                        }).toArray(Repository[]::new);

                repositories = allRepositories;

                loading = false;
            });
        }
    }

    @JsMethod
    public void onRepositoryDelete(String name) {
        allRepositories = Arrays.stream(allRepositories)
                .filter(repository -> !repository.nameWithOwner.equals(name))
                .toArray(Repository[]::new);

        repositories = allRepositories;

        userManagerRpcClient.deleteFavoriteRepository(name);
    }
}
