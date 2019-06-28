package ru.githarbor.frontend.harbor.core.github.request;

import io.reactivex.Observable;
import io.reactivex.Single;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.github.data.PageInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Singleton
public class BranchesRequest {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Result {
        public Resource resource;
    }


    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Resource {
        public Refs refs;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Refs {
        public PageInfo pageInfo;
        public Branch[] branches;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Branch {
        public String name;

        @JsProperty(name = "target.oid")
        public String oid;

        @JsProperty(name = "target.committedDate")
        public String committedDate;
    }

    private GitHubGqlClient client;

    @Inject
    public BranchesRequest(GitHubGqlClient client) {
        this.client = client;
    }

    public Single<BranchesRequest.Branch[]> execute(String ownerWithProject) {
        return execute(ownerWithProject, null).toObservable()
                .flatMap(refs -> getMoreBranches(ownerWithProject, refs))
                .collect((Callable<ArrayList<Branch>>) ArrayList::new, (arrayList, refs) -> arrayList.addAll(Arrays.asList(refs.branches)))
                .map(branches1 -> branches1.toArray(new Branch[0]));
    }

    private Observable<BranchesRequest.Refs> getMoreBranches(String ownerWithProject, BranchesRequest.Refs refs) {
        if (refs.pageInfo.hasNextPage) {
            return Observable.merge(
                    Observable.just(refs),
                    execute(ownerWithProject, refs.pageInfo.endCursor).toObservable()
                            .flatMap(refs1 -> getMoreBranches(ownerWithProject, refs1)));
        }

        return Observable.just(refs);
    }

    private Single<BranchesRequest.Refs> execute(String ownerWithProject, String after) {
        return client.<BranchesRequest.Result>request("query Branches($url: URI!, $after: String = null) {\n" +
                "  rateLimit {\n" +
                "    limit\n" +
                "    remaining\n" +
                "    resetAt\n" +
                "  }\n" +
                "  resource(url: $url) {\n" +
                "    ... on Repository {\n" +
                "      refs(first: 100, refPrefix: \"refs/heads/\", after: $after) {\n" +
                "        pageInfo {\n" +
                "          endCursor\n" +
                "          hasNextPage\n" +
                "        }\n" +
                "        branches: nodes {\n" +
                "          name\n" +
                "          target {\n" +
                "            ... on Commit {\n" +
                "              committedDate\n" +
                "            }\n" +
                "            oid\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n", JsPropertyMap.of("url", ownerWithProject, "after", after))
                .map(result -> result.resource.refs);
    }
}
