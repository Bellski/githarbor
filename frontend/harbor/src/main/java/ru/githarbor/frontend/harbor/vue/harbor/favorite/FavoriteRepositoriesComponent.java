package ru.githarbor.frontend.harbor.vue.harbor.favorite;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import ru.githarbor.frontend.github.request.FavoriteRepositoriesRequest;
import ru.githarbor.frontend.github.request.RepositorySearchRequest;
import ru.githarbor.frontend.harbor.core.github.core.RepositoryInfo;
import ru.githarbor.frontend.harbor.core.rpc.UserManagerRpcClient;
import ru.githarbor.frontend.harbor.jslib.HarborGlobal;
import ru.githarbor.frontend.harbor.vue.harbor.repository.RepositoryTreeSharedState;
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
    public ru.githarbor.frontend.harbor.core.github.core.Repository repository;

    @Inject
    public UserManagerRpcClient userManagerRpcClient;

    @Data
    @Inject
    public RepositoryTreeSharedState repositoryTreeSharedState;

    @Data
    public boolean loading;

    @Data
    public String input;

    @Data
    public Repository[] repositories = new Repository[0];

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
        vue().$watch(() -> repositoryTreeSharedState.inFavorites, (newValue, oldValue) -> {
            if (newValue) {
                final RepositoryInfo repositoryInfo = repository.info;

                final Repository[] newRepositories = new Repository[repositories.length + 1];
                newRepositories[0] =  new Repository(
                        repositoryInfo.nameWithOwner.ownerWithName,
                        "",
                        repositoryInfo.primaryLanguage,
                        HarborGlobal.kFormat(Double.valueOf(repositoryInfo.stars)),
                        HarborGlobal.timeAgo("", "en")
                );

                for (int i = 0; i < repositories.length; i++) {
                    newRepositories[i + 1] = repositories[i];
                }

                allRepositories = newRepositories;

                repositories = allRepositories;
            } else {
                allRepositories =  Arrays.stream(repositories)
                        .filter(repository -> !this.repository.info.nameWithOwner.ownerWithName.equals(repository.nameWithOwner))
                        .toArray(Repository[]::new);

                repositories = allRepositories;
            }
        });

        if (user.favoriteRepositories != null) {
            loading = true;

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

        if (name.equals(repository.info.nameWithOwner.ownerWithName)) {
            repository.info.inFavorite = false;
            repositoryTreeSharedState.inFavorites = false;
        }
    }
}
