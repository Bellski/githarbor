package ru.githarbor.frontend.harbor.core.github.core;

import ru.githarbor.frontend.harbor.core.github.request.RepositoryExistsRequest;

public class RepositoryInfo {
    public final OwnerWithName nameWithOwner;
    public final String homePage;
    public final String description;
    public final String primaryLanguage;
    public final String stars;
    public boolean inFavorite;

    public RepositoryInfo(RepositoryExistsRequest.Repository repository, boolean inFavorites) {
        nameWithOwner = new OwnerWithName(repository.nameWithOwner);
        homePage = repository.homepage;
        description = repository.description;
        primaryLanguage = repository.primaryLanguage != null ? repository.primaryLanguage.name : null;
        stars = repository.stars;
        this.inFavorite = inFavorites;
    }

    @Override
    public String toString() {
        return nameWithOwner.ownerWithName;
    }
}
