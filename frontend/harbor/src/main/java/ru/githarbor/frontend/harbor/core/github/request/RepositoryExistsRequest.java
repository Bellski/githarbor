package ru.githarbor.frontend.harbor.core.github.request;

import io.reactivex.Single;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.github.client.GitHubGqlClient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RepositoryExistsRequest {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Result {
        public Repository repository;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Repository {
        public String nameWithOwner;
        public String homepage;
        public String description;

        @JsProperty(name = "stargazers.totalCount")
        public String stars;

        public Language primaryLanguage;
        public Branch defaultBranch;
        public Branch currentBranch;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Language {
        public String name;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Branch {
        public String name;

        @JsProperty(name = "target.oid")
        public String oid;

        @JsProperty(name = "target.committedDate")
        public String committedDate;
    }


    private final GitHubGqlClient client;

    @Inject
    public RepositoryExistsRequest(GitHubGqlClient client) {
        this.client = client;
    }


    public Single<Repository> execute(String ownerWithProject, String currentBranch) {
        return client.<Result>request("query RepositoryExists($url: URI!, $branch: String = \"\") {\n" +
                "  rateLimit {\n" +
                "    limit\n" +
                "    remaining\n" +
                "    resetAt\n" +
                "  }\n" +
                "  repository: resource(url: $url) {\n" +
                "    ... on Repository {\n" +
                "      nameWithOwner\n" +
                "      homepage: homepageUrl\n" +
                "      description: descriptionHTML\n" +
                "      stargazers {\n" +
                "        totalCount\n" +
                "      }\n" +
                "      primaryLanguage {\n" +
                "        name\n" +
                "      }\n" +
                "      ...defaultBranch\n" +
                "      currentBranch: ref(qualifiedName: $branch) {\n" +
                "        name\n" +
                "        target {\n" +
                "          ... on Commit {\n" +
                "            oid\n" +
                "            committedDate\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "fragment defaultBranch on Repository {\n" +
                "  defaultBranch: defaultBranchRef {\n" +
                "    name\n" +
                "    target {\n" +
                "      ... on Commit {\n" +
                "        oid\n" +
                "        committedDate\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n", JsPropertyMap.of("url", ownerWithProject, "branch", currentBranch))
                .map(result -> result.repository);
    }
}
