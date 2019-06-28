package ru.githarbor.frontend.harbor.vue.harbor.repository;

import ru.githarbor.frontend.harbor.core.github.core.Repository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RepositoryTreeSharedState {

    public boolean inFavorites;

    @Inject
    public RepositoryTreeSharedState(Repository repository) {
        inFavorites = repository.info.inFavorite;
    }
}
