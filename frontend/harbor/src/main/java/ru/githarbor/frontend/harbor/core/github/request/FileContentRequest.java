package ru.githarbor.frontend.harbor.core.github.request;

import elemental2.core.JsArray;
import io.reactivex.Single;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.github.client.GitHubGqlClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.StringJoiner;

@Singleton
public class FileContentRequest {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Result {
        private BlobResource resource;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class BlobResource {
        public Blob blob;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Blob {
        public String text;
    }

    private GitHubGqlClient client;

    @Inject
    public FileContentRequest(GitHubGqlClient client) {
        this.client = client;
    }

    public Single<Blob> execute(String branch, String ownerWithName, String path) {
        return client.<Result>request("query getBlobContent($url: URI!, $path:String!) {\n" +
                        "  rateLimit {\n" +
                        "    limit,\n" +
                        "    remaining,\n" +
                        "    resetAt\n" +
                        "  }\n" +
                        "  resource(url: $url) {\n" +
                        "    ... on Repository {\n" +
                        "      blob: object(expression: $path) {\n" +
                        "        ... on Blob {\n" +
                        "          text\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                JsPropertyMap.of(
                        "url", ownerWithName,
                        "path", branch + ":" + path
                ))
                .map(result -> result.resource.blob);
    }

    public Single<Blob[]> executeBatch(String branch, String ownerWithName, String... paths) {
        return client.<JsPropertyMap>request("query getBlobContent($url: URI!) {\n" +
                        "  rateLimit {\n" +
                        "    limit,\n" +
                        "    remaining,\n" +
                        "    resetAt\n" +
                        "  }\n" +
                        "  resource(url: $url) {\n" +
                        "    ... on Repository {\n" +
                        buildBatch(branch, paths) +
                        "    }\n" +
                        "  }\n" +
                        "}",
                JsPropertyMap.of("url", ownerWithName))
                .map(jsPropertyMap -> {
                    final JsPropertyMap<Blob> resourceMap = Js.cast(jsPropertyMap.get("resource"));

                    final JsArray<Blob> blobs = new JsArray<>();

                    for (int i = 0; i < paths.length; i++) {
                        blobs.push(resourceMap.get("_" + i));
                    }

                    return Js.uncheckedCast(blobs.slice());
                });
    }

    private String buildBatch(String branch, String... paths) {
        final StringJoiner builder = new StringJoiner("\n");

        for (int i = 0; i < paths.length; i++) {
            builder.add("      _" + i + ": object(expression: \"" + branch + ":" + paths[i] + "\") {\n" +
                    "        ... on Blob {\n" +
                    "          text\n" +
                    "        }\n" +
                    "      }");
        }

        return builder.toString();
    }
}