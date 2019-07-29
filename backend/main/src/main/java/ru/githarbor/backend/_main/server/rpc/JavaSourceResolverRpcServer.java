package ru.githarbor.backend._main.server.rpc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.javalin.Context;
import io.javalin.Handler;
import org.jetbrains.annotations.NotNull;
import ru.githarbor.backend._main.server.resolver.JavaSourceResolver;
import ru.githarbor.shared.JavaSourceMetadataDTO;
import ru.githarbor.shared.rpc.java.ResolveSourceRequest;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;


@Singleton
public class JavaSourceResolverRpcServer implements Handler {

    private static final String QUERY = "query ($url: URI!, $path: String!) {" +
            "  resource(url: $url) {" +
            "    ... on Repository {" +
            "      object(expression: $path) {" +
            "        ... on Blob {" +
            "          text" +
            "        }" +
            "      }" +
            "    }" +
            "  }" +
            "}";

    private final Gson gson;
    public static final HttpClient httpClient = HttpClient.newHttpClient();

    @Inject
    public JavaSourceResolverRpcServer(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        final ResolveSourceRequest resolveSourceRequest = gson.fromJson(ctx.body(), ResolveSourceRequest.class);

        if (ResolveSourceRequest.class.getName().equals(resolveSourceRequest.methodName)) {
            final HttpRequest request = buildRequest(buildQuery(resolveSourceRequest.ownerWithName, resolveSourceRequest.path), ctx.sessionAttribute("accessToken"));

            final String blobContent = getBlobTextFromResponse(gson, httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body());

            if (blobContent == null) {
                return;
            }

            final Optional<JavaSourceMetadataDTO> resolve = JavaSourceResolver.resolve(blobContent);

            if (resolve.isPresent()) {
                ctx.result(gson.toJson(resolve.get()));
            }
        }
    }

    public static HttpRequest buildRequest(String query, String token) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/graphql"))
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "token " + token)
                .POST(HttpRequest.BodyPublishers.ofString(query))
                .build();
    }

    public static String buildQuery(String ownerWithName, String path) {
        final JsonObject variables = new JsonObject();
        variables.addProperty("url", ownerWithName);
        variables.addProperty("path", path);

        final JsonObject query = new JsonObject();
        query.addProperty("query", QUERY);
        query.add("variables", variables);

        return query.toString();
    }

    public static String getBlobTextFromResponse(Gson gson, String responseBody) {
        final JsonObject response = gson.fromJson(responseBody, JsonObject.class);

        final JsonElement dataElement = response.get("data");

        if (dataElement == null) {
            return null;
        }

        final JsonElement resourceElement = dataElement.getAsJsonObject().get("resource");

        if (resourceElement == null) {
            return null;
        }

        final JsonElement objectElement = resourceElement.getAsJsonObject().get("object");

        if (objectElement == null) {
            return null;
        }

        final JsonElement textElement = objectElement.getAsJsonObject().get("text");

        if (textElement == null) {
            return null;
        }

        return textElement.getAsString();
    }
}
