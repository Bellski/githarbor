package ru.githarbor.frontend.harbor.core.service;

import ru.githarbor.frontend.harbor.core.github.request.RepositoryExistsRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RepositoryResolverService {

    private final RepositoryExistsRequest repositoryExistsRequest;
    private final RepositoryPathsService repositoryPathsService;

    @Inject
    public RepositoryResolverService(RepositoryExistsRequest repositoryExistsRequest, RepositoryPathsService repositoryPathsService) {
        this.repositoryExistsRequest = repositoryExistsRequest;
        this.repositoryPathsService = repositoryPathsService;
    }

    public void resolve(String ownerWithName, String branchToResolve) {
        repositoryExistsRequest.execute(ownerWithName, branchToResolve).subscribe(repository -> {

        });
    }
}
