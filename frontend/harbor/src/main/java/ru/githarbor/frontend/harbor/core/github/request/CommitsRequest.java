package ru.githarbor.frontend.harbor.core.github.request;

import elemental2.core.JsDate;
import io.reactivex.Single;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.harbor.core.github.core.Repository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CommitsRequest {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Data {

        @JsProperty(name = "repository.ref.target.history")
        public History history;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class PageInfo {
        public boolean hasNextPage;
        public String endCursor;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class History {
        public double totalCount;
        public PageInfo pageInfo;
        public Edge[] edges;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Edge {
        public Node node;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Node {
        public String message;

        public String oid;

        @JsProperty(name = "author.user")
        private JsPropertyMap user;

        @JsProperty(name = "author.user.login")
        private String login;

        public String committedDate;

        @JsOverlay
        public final String getDate() {
            return new JsDate(committedDate).toLocaleDateString() + " " + new JsDate(committedDate).toLocaleTimeString();
        }

        @JsOverlay
        public final String getCommitter() {
            return user == null ? "" : login;
        }
    }

    private final GitHubGqlClient client;
    private final Repository repository;

    @Inject
    public CommitsRequest(GitHubGqlClient client, Repository repository) {
        this.client = client;
        this.repository = repository;
    }

    public Single<History> execute(String path) {
        return execute(path, null);
    }

    public Single<History> execute(String path, String endCursor) {
        final JsPropertyMap<String> values = Js.cast(JsPropertyMap.of());
        values.set("owner", repository.info.nameWithOwner.owner);
        values.set("name", repository.info.nameWithOwner.name);
        values.set("path", path);
        values.set("branch", repository.getCurrentBranch().name);

        if (endCursor != null) {
            values.set("endCursor", endCursor);
        }

        return client.<Data>request("query Commits($owner: String!, $name: String!, $path: String, $branch: String!, $endCursor: String) {\n" +
                "  rateLimit {\n" +
                "    limit\n" +
                "    remaining\n" +
                "    resetAt\n" +
                "  }\n" +
                "  repository(owner: $owner, name: $name) {\n" +
                "    ref(qualifiedName: $branch) {\n" +
                "      target {\n" +
                "        ... on Commit {  \n" +
                "          history(first: 100, path: $path, after:$endCursor) {\n" +
                "            totalCount,\n" +
                "            pageInfo {\n" +
                "              hasNextPage,\n" +
                "              endCursor\n" +
                "            }\n" +
                "            edges {\n" +
                "              node {\n" +
                "                oid\n" +
                "                message\n" +
                "                author {\n" +
                "                  user {\n" +
                "                    login\n" +
                "                  }\n" +
                "                }\n" +
                "                committedDate\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n", values).map(data -> data.history);
    }
}
