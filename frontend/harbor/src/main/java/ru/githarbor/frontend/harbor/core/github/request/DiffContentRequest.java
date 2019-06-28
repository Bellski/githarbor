package ru.githarbor.frontend.harbor.core.github.request;

import io.reactivex.Single;
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
public class DiffContentRequest {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Data {

        @JsProperty(name = "repository.modifiedContent")
        public BlobData modifiedContent;

        @JsProperty(name = "repository.originalContent")
        public BlobData originalContent;

    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class BlobData {
        public String oid;
        public String text;
    }


    private GitHubGqlClient client;
    private Repository repository;

    @Inject
    public DiffContentRequest(GitHubGqlClient client, Repository repository) {
        this.client = client;
        this.repository = repository;
    }

    public Single<Data> execute(String modified, String filename) {
        final JsPropertyMap<String> values = Js.cast(JsPropertyMap.of());
        values.set("owner", repository.info.nameWithOwner.owner);
        values.set("name", repository.info.nameWithOwner.name);
        values.set("modified", modified + ":" + filename);
        values.set("original", repository.getCurrentBranch().name + ":" + filename);

        return client.request("query CommitContentDiff($owner: String!, $name: String!, $modified: String!, $original: String!) {\n" +
                "  rateLimit {\n" +
                "    limit\n" +
                "    remaining\n" +
                "    resetAt\n" +
                "  }\n" +
                "  repository(owner: $owner, name: $name) {\n" +
                "    modifiedContent: object(expression: $modified) {\n" +
                "      ... on Blob {\n" +
                "        oid\n" +
                "        text\n" +
                "      }\n" +
                "    }\n" +
                "    originalContent: object(expression: $original) {\n" +
                "      ... on Blob {\n" +
                "        oid\n" +
                "        text\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n", values);
    }
}
