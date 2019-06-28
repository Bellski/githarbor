package ru.githarbor.frontend.harbor.core.github.core;

import io.reactivex.Single;
import ru.githarbor.frontend.harbor.core.github.request.BranchesRequest;
import ru.githarbor.frontend.harbor.core.rpc.UserManagerRpcClient;

import java.util.HashMap;
import java.util.Map;

public class Repository {
    public final RepositoryInfo info;

    private final Branch defaultBranch;
    private Branch currentBranch;

    private final BranchesRequest branchesRequest;
    private final UserManagerRpcClient userManagerRpcClient;

    private boolean resolvedBranches = false;

    private Map<String, Branch> branchByName = new HashMap<>();

    public Repository(RepositoryInfo info,
                      Branch defaultBranch,
                      Branch currentBranch,
                      BranchesRequest branchesRequest,
                      UserManagerRpcClient userManagerRpcClient) {
        this.info = info;
        this.defaultBranch = defaultBranch;
        this.currentBranch = currentBranch;
        this.branchesRequest = branchesRequest;
        this.userManagerRpcClient = userManagerRpcClient;

        branchByName.put(defaultBranch.name, defaultBranch);
        branchByName.put(currentBranch.name, currentBranch);
    }

    public Branch getCurrentBranch() {
        return currentBranch;
    }

    public Branch setCurrentBranch(String branchName) {
        currentBranch = branchByName.get(branchName);

        return currentBranch;
    }

    public Single<Branch[]> resolveBranches() {
        if (resolvedBranches) {
            return Single.just(branchByName.values().toArray(new Branch[0]));
        }

        return branchesRequest.execute(info.nameWithOwner.ownerWithName)
                .map(branches -> {
                    for (BranchesRequest.Branch branch : branches) {
                        branchByName.put(branch.name, new Branch(
                                info.nameWithOwner,
                                branch.name,
                                branch.oid,
                                branch.committedDate,
                                defaultBranch.repositoryPathsService,
                                defaultBranch.fileContentRequest
                        ));
                    }

                    return branchByName.values().toArray(new Branch[0]);
                });
    }

    public boolean setFavorite() {
        if (!info.inFavorite) {
            userManagerRpcClient.addFavoriteRepository(info.nameWithOwner.ownerWithName);
        } else {
            userManagerRpcClient.deleteFavoriteRepository(info.nameWithOwner.ownerWithName);
        }

        return info.inFavorite = !info.inFavorite;
    }

}
