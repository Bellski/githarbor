package ru.githarbor.frontend.github.request;

import io.reactivex.Single;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.github.data.Data;
import ru.githarbor.frontend.github.data.PageInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RepositorySearchRequest {

    private final GitHubGqlClient client;

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Result extends Data {
        public Search search;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Search {
        public PageInfo pageInfo;
        public Repository[] repositories;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Repository {
        public String nameWithOwner;
        public String description;
        public PrimaryLanguage primaryLanguage;

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
    public RepositorySearchRequest(GitHubGqlClient client) {
        this.client = client;
    }

    public Single<Search> execute(String query) {
        return client.<Result>request("query FindRepository($query: String!) {\n" +
                "  rateLimit {\n" +
                "    limit\n" +
                "    remaining\n" +
                "    resetAt\n" +
                "  }\n" +
                "  search(query: $query, type: REPOSITORY, first: 20) {\n" +
                "    pageInfo {\n" +
                "      endCursor\n" +
                "      hasNextPage\n" +
                "    }\n" +
                "    repositories: nodes {\n" +
                "      ... on Repository {\n" +
                "        nameWithOwner\n" +
                "        description\n" +
                "        primaryLanguage {\n" +
                "          color\n" +
                "          name\n" +
                "        }\n" +
                "        stargazers {\n" +
                "          totalCount\n" +
                "        }\n" +
                "        updatedAt\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n", JsPropertyMap.of("query", query))
                .map(data -> data.search);
    }
}
