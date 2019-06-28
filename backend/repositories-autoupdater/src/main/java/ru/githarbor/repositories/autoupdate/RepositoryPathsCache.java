package ru.githarbor.repositories.autoupdate;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import ru.githarbor.repositories.autoupdate.data.RepositorySearchResult;
import ru.githarbor.repositories.autoupdate.fetch.RepositoryTreeListFetcher;
import ru.githarbor.repositories.autoupdate.fetch.ToCacheRepositoriesFetcher;
import ru.githarbor.repositories.autoupdate.gzip.Gzip;
import ru.githarbor.repositories.autoupdate.manager.RepositoryCacheManager;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RepositoryPathsCache {

    private class GitHubRepository {

        public final RepositorySearchResult.Repository repository;
        public final List<String> paths;

        public GitHubRepository(RepositorySearchResult.Repository repository, List<String> paths) {
            this.repository = repository;
            this.paths = paths;
        }
    }

    private static final String accessToke = "2b8c672e8ad5f79067994f2bb267fb1f974d66a4";

    private final RepositoryTreeListFetcher repositoryTreeListFetcher;
    private final RepositoryCacheManager repositoryCacheManager;

    public RepositoryPathsCache(RepositoryTreeListFetcher repositoryTreeListFetcher, RepositoryCacheManager repositoryCacheManager) {
        this.repositoryTreeListFetcher = repositoryTreeListFetcher;
        this.repositoryCacheManager = repositoryCacheManager;
    }


    public void initCache() throws IOException, InterruptedException {
        fetchAutoUpdateRepositories().blockingForEach(gitHubRepository -> {
            System.out.println("--------------------------->>>>>>>>>>>>>> " + gitHubRepository.repository.nameWithOwner);

            repositoryCacheManager.createAutoUpdateRepositoryCache(
                    gitHubRepository.repository.nameWithOwner,
                    gitHubRepository.repository.branch.name,
                    Instant.parse(gitHubRepository.repository.branch.target.committedDate).getEpochSecond(),
                    Gzip.compress(String.join(",", gitHubRepository.paths))
            );
        });
    }

    private Observable<GitHubRepository> fetchAutoUpdateRepositories() throws IOException, InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(3);
        final Scheduler scheduler = Schedulers.from(executorService);

        final List<Observable<GitHubRepository>> tasks = new ArrayList<>();

        for (RepositorySearchResult.Repository repository : ToCacheRepositoriesFetcher.fetchRepositories()) {
            final String nameWithOwner = repository.nameWithOwner;
            final String branch = repository.branch.name;

            tasks.add(Observable.defer(() -> Observable.just(new GitHubRepository(repository, repositoryTreeListFetcher.fetch(nameWithOwner, branch, accessToke)))));
        }

        return Observable.fromIterable(tasks)
                .flatMap(gitHubRepositoryObservable -> gitHubRepositoryObservable.subscribeOn(scheduler))
                .filter(gitHubRepository -> gitHubRepository.paths.size() >= 20000)
                .doOnComplete(() -> {
                    System.out.println("complete");
                    executorService.shutdown();
                    scheduler.shutdown();
                });
    }
}
