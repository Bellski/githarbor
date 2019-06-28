package ru.githarbor.backend._main.server.ws;

import io.javalin.websocket.WsSession;
import ru.githarbor.backend._main.manager.RepositoryPathsManager;
import ru.githarbor.backend._main.manager.RepositoryTaskManager;
import ru.githarbor.repositories.autoupdate.data.RepositoryCache;
import ru.githarbor.repositories.autoupdate.fetch.RepositoryTreeListFetcher;
import ru.githarbor.repositories.autoupdate.gzip.Gzip;
import ru.githarbor.repositories.autoupdate.manager.RepositoryCacheManager;
import ru.githarbor.shared.paths.Branch;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Singleton
public class RepositoryPathsService {

    private final Map<String, WsSession> pathsRequesters;
    private final RepositoryTaskManager repositoryTasksManager;
    private final RepositoryPathsManager repositoryPathsManager;
    private final RepositoryCacheManager repositoryCacheManager;

    private final RepositoryTreeListFetcher repositoryTreeListFetcher;

    @Inject
    public RepositoryPathsService(Map<String, WsSession> pathsRequesters,
                                  RepositoryTaskManager repositoryTasksManager,
                                  RepositoryPathsManager repositoryPathsManager,
                                  RepositoryCacheManager repositoryCacheManager,
                                  RepositoryTreeListFetcher repositoryTreeListFetcher) {

        this.pathsRequesters = pathsRequesters;
        this.repositoryTasksManager = repositoryTasksManager;
        this.repositoryPathsManager = repositoryPathsManager;
        this.repositoryCacheManager = repositoryCacheManager;
        this.repositoryTreeListFetcher = repositoryTreeListFetcher;
    }

    public void repositoryPaths(Branch branch, WsSession wsSession, long userId, String accessToken) {
        if (repositoryTasksManager.existsTask(branch.ownerWithName, branch.branch)) {
            repositoryTasksManager.addSubscriber(branch.ownerWithName, branch.branch, wsSession.getId());
        } else {
            final RepositoryCache repositoryCache = repositoryCacheManager.getRepositoryCache(branch.ownerWithName);

            if (repositoryCache == null) {
                if (wsSession.isOpen()) {
                    wsSession.send("0");
                }

                doOnRepositoryNotFound(branch, wsSession, userId, accessToken);

                return;
            }

            final RepositoryCache.Branch[] branches = repositoryCache.branches == null ? new RepositoryCache.Branch[0] : repositoryCache.branches;
            final RepositoryCache.Branch branchCache = Arrays.stream(branches)
                    .filter(branch1 -> branch1.name.equals(branch.branch))
                    .findFirst()
                    .orElse(null);

            if (branchCache == null || branchCache.lastCommit != Instant.parse(branch.committedDate).getEpochSecond()) {
                if (wsSession.isOpen()) {
                    wsSession.send("1");
                }

                doOnBranchNotFound(branch, wsSession, userId, accessToken, branchCache == null);

                return;
            }


            if (wsSession.isOpen()) {
                wsSession.send(ByteBuffer.wrap(repositoryPathsManager.getPaths(branch.ownerWithName, branch.branch)));
            } else {
                pathsRequesters.remove(wsSession.getId());
            }
        }
    }

    private void doOnRepositoryNotFound(Branch branch, WsSession wsSession, long userId, String accessToken) {
        final long taskId = repositoryTasksManager.createTask(branch.ownerWithName, branch.branch, userId);

        if (taskId != -1) {
            try {
                final List<String> paths = repositoryTreeListFetcher.fetch(branch.ownerWithName, branch.branch, accessToken, () -> {
                    if (wsSession.isOpen()) {
                        wsSession.send("3");
                    }
                });

                byte[] gZippedPaths = Gzip.compress(String.join(",", paths));
                final ByteBuffer gZippedPathsBb = ByteBuffer.wrap(gZippedPaths);


                repositoryCacheManager.createRepositoryCache(
                        branch.ownerWithName,
                        branch.branch,
                        Instant.parse(branch.committedDate).getEpochSecond(),
                        gZippedPaths,
                        false
                );

                if (wsSession.isOpen()) {
                    wsSession.send(gZippedPathsBb);
                }

                final String[] subscribers = repositoryTasksManager.getSubscribers(branch.ownerWithName, branch.branch);
                repositoryTasksManager.deleteTask(taskId);

                for (String subscriber : subscribers) {
                    final WsSession subscriberSession = pathsRequesters.get(subscriber);

                    if (subscriberSession != null && subscriberSession.isOpen()) {
                        subscriberSession.send(gZippedPathsBb);
                    }
                }

            } catch (Exception e) {
                repositoryTasksManager.deleteTask(taskId);

                e.printStackTrace();

                throw new RuntimeException(e);
            }
        } else {
            repositoryTasksManager.addSubscriber(branch.ownerWithName, branch.branch, wsSession.getId());
        }
    }


    private void doOnBranchNotFound(Branch branch, WsSession wsSession, long userId, String accessToken, boolean newBranch) {
        final long taskId = repositoryTasksManager.createTask(branch.ownerWithName, branch.branch, userId);

        if (taskId != -1) {
            try {
                final List<String> paths = repositoryTreeListFetcher.fetch(branch.ownerWithName, branch.branch, accessToken, () -> {
                    if (wsSession.isOpen()) {
                        wsSession.send("3");
                    }
                });

                byte[] gZippedPaths = Gzip.compress(String.join(",", paths));
                final ByteBuffer gZippedPathsBb = ByteBuffer.wrap(gZippedPaths);

                if (newBranch) {
                    repositoryCacheManager.addBranchPaths(
                            branch.ownerWithName,
                            branch.branch,
                            Instant.parse(branch.committedDate).getEpochSecond(),
                            gZippedPaths
                    );
                } else {
                    repositoryCacheManager.updateBranchPaths(
                            branch.ownerWithName,
                            branch.branch,
                            Instant.parse(branch.committedDate).getEpochSecond(),
                            gZippedPaths
                    );
                }

                if (wsSession.isOpen()) {
                    wsSession.send(gZippedPathsBb);
                }

                final String[] subscribers = repositoryTasksManager.getSubscribers(branch.ownerWithName, branch.branch);
                repositoryTasksManager.deleteTask(taskId);

                for (String subscriber : subscribers) {
                    final WsSession subscriberSession = pathsRequesters.get(subscriber);

                    if (subscriberSession != null && subscriberSession.isOpen()) {
                        subscriberSession.send(gZippedPathsBb);
                    }
                }

            } catch (Exception e) {
                repositoryTasksManager.deleteTask(taskId);

                e.printStackTrace();

                throw new RuntimeException(e);
            }
        } else {
            repositoryTasksManager.addSubscriber(branch.ownerWithName, branch.branch, wsSession.getId());
        }
    }
}
