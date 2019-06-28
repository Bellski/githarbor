package ru.githarbor.repositories.autoupdate.fetch;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.githarbor.repositories.autoupdate.data.RepositorySearchResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ToCacheRepositoriesFetcher {
    private static final Gson GSON = new Gson();

    public static List<RepositorySearchResult.Repository> fetchRepositories() throws IOException, InterruptedException {
        final List<RepositorySearchResult.Repository> fetchedRepositories = new ArrayList<>();

        final HttpClient httpClient = HttpClient.newHttpClient();

        RepositorySearchResult searchResult = getSearchResultFromResponse(httpClient.send(buildRequest(buildQuery(null)), HttpResponse.BodyHandlers.ofString()));
        Collections.addAll(fetchedRepositories, searchResult.repositories);

        while (searchResult.pageInfo.hasNextPage) {
            searchResult = getSearchResultFromResponse(httpClient.send(buildRequest(buildQuery(searchResult.pageInfo.endCursor)), HttpResponse.BodyHandlers.ofString()));

            Collections.addAll(fetchedRepositories, searchResult.repositories);
        }


        return fetchedRepositories.stream()
                .filter(repository -> !repository.nameWithOwner.equals("cdnjs/cdnjs")).collect(Collectors.toList());
    }


    private static HttpRequest buildRequest(String query) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/graphql"))
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "token 2b8c672e8ad5f79067994f2bb267fb1f974d66a4")
                .POST(HttpRequest.BodyPublishers.ofString(query))
                .build();
    }

    private static String buildQuery(String cursor) {
        StringBuilder queryBuilder = new StringBuilder()
                .append("{")
                .append("\"query\":")
                .append("\"")
                .append("query repositories {");

        if (cursor != null) {
            queryBuilder.append("  search(type:REPOSITORY, first:100, query:\\\"stars:>3000 size:>100000\\\", after:\\\"")
                    .append(cursor)
                    .append("\\\") {");
        } else {
            queryBuilder.append("  search(type:REPOSITORY, first:100, query:\\\"stars:>3000 size:>100000\\\") {");
        }

        queryBuilder.append(" pageInfo {" +
                "      hasNextPage" +
                "      endCursor" +
                "    }" +
                "    repositories: nodes {" +
                "      ... on Repository {" +
                "        nameWithOwner" +
                "        branch: defaultBranchRef {" +
                "          name" +
                "          target {" +
                "            ... on Commit {" +
                "              committedDate" +
                "            }" +
                "          }" +
                "        }" +
                "      }" +
                "    }" +
                "  }" +
                "}")
                .append("\",")
                .append("\"variables\": {}")
                .append("}");

        return queryBuilder.toString();
    }

    private static RepositorySearchResult getSearchResultFromResponse(HttpResponse<String> httpResponse) {
        return GSON.fromJson(GSON.fromJson(httpResponse.body(), JsonObject.class)
                .getAsJsonObject("data")
                .getAsJsonObject("search"), RepositorySearchResult.class);
    }
}
