package ru.githarbor.frontend.fileviewer.core;

import io.reactivex.Single;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

import javax.inject.Inject;
import javax.inject.Singleton;

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

    public Single<Blob> execute(String ownerWithName, String branch, String path) {
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
                .map(result -> {

                    if (result.resource == null || result.resource.blob == null || result.resource.blob.text == null) {
                        throw new RuntimeException("No text");
                    }

                    return result.resource.blob;
                });
    }
}