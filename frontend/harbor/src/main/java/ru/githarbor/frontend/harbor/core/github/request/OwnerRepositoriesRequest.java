package ru.githarbor.frontend.harbor.core.github.request;

import io.reactivex.Observable;
import io.reactivex.Single;
import jsinterop.annotations.*;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.github.data.PageInfo;
import ru.githarbor.frontend.github.request.RepositorySearchRequest;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;

@Singleton
public class OwnerRepositoriesRequest {

    private User user;
    private GitHubGqlClient client;

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Result {
        @JsProperty(name = "repositoryOwner.repositories")
        public Repositories repositories;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Repositories {
        public PageInfo pageInfo;
        public Repository[] nodes;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Repository {
        public String nameWithOwner;
        public String description;
        public RepositorySearchRequest.PrimaryLanguage primaryLanguage;

        @JsProperty(name = "stargazers.totalCount")
        public double stars;

        public String updatedAt;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class PrimaryLanguage {
        public String color;
        public String name;
    }


    @Inject
    public OwnerRepositoriesRequest(User user, GitHubGqlClient client) {
        this.user = user;
        this.client = client;
    }

    public Single<Repository[]> execute(String owner) {
        return getRepositories(owner)
                .toObservable()
                .flatMap(repositories -> getRepositories(owner, repositories))
                .collect(ArrayList::new,(repositories, repositories2) -> repositories.addAll(Arrays.asList(repositories2.nodes)))
                .map(objects -> objects.toArray(new Repository[0]));
    }

    private Single<Repositories> getRepositories(String owner) {
        return client.<Result>request("query OwnerRepositories($owner: String!, $after: String) {\n" +
                "  rateLimit {\n" +
                "    limit\n" +
                "    remaining\n" +
                "    resetAt\n" +
                "  }\n" +
                "  repositoryOwner(login: $owner) {\n" +
                "    repositories(first: 100, orderBy: {field: PUSHED_AT, direction: DESC}, after: $after) {\n" +
                "      pageInfo {\n" +
                "        endCursor\n" +
                "        hasNextPage\n" +
                "      }\n" +
                "      nodes {\n" +
                "         nameWithOwner,\n" +
                "          description,\n" +
                "          primaryLanguage {\n" +
                "            color\n" +
                "            name\n" +
                "          }\n" +
                "          stargazers {\n" +
                "            totalCount\n" +
                "          }\n" +
                "          updatedAt\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n", JsPropertyMap.of("owner", owner))
                .map(data -> data.repositories);
    }

    private Observable<Repositories> getRepositories(String owner, Repositories after) {
        if (!after.pageInfo.hasNextPage) {
            return Observable.just(after);
        }

        final Single<Repositories> request = client.<Result>request("query OwnerRepositories($owner: String!, $after: String) {\n" +
                "  rateLimit {\n" +
                "    limit\n" +
                "    remaining\n" +
                "    resetAt\n" +
                "  }\n" +
                "  repositoryOwner(login: $owner) {\n" +
                "    repositories(first: 100, orderBy: {field: PUSHED_AT, direction: DESC}, after: $after) {\n" +
                "      pageInfo {\n" +
                "        endCursor\n" +
                "        hasNextPage\n" +
                "      }\n" +
                "      nodes {\n" +
                "         nameWithOwner,\n" +
                "          description,\n" +
                "          primaryLanguage {\n" +
                "            color\n" +
                "            name\n" +
                "          }\n" +
                "          stargazers {\n" +
                "            totalCount\n" +
                "          }\n" +
                "          updatedAt\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n", JsPropertyMap.of("owner", owner, "after", after.pageInfo.endCursor))
                .map(data -> data.repositories);

        return Observable.merge(
                Observable.just(after),
                request.toObservable()
                        .flatMap(repositories -> getRepositories(owner, repositories))
        );
    }
}