package ru.githarbor.frontend.harbor.core.github.request;

import elemental2.core.JsArray;
import elemental2.core.JsObject;
import io.reactivex.Single;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.github.request.RepositorySearchRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FavoriteRepositoriesRequest {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Repository {
        public String nameWithOwner;
        public String description;
        public RepositorySearchRequest.PrimaryLanguage primaryLanguage;

        @JsProperty(name = "stargazers.totalCount")
        public double stars;

        public String updatedAt;
    }

    private final GitHubGqlClient client;

    @Inject
    public FavoriteRepositoriesRequest(GitHubGqlClient client) {
        this.client = client;
    }

    public Single<JsArray<Repository>> execute(String[] repositories) {
        final StringBuilder repositoryQueryPartsBuilder = new StringBuilder();

        for (int i = 0; i < repositories.length; i++) {
            final String[] ownerWithName = repositories[i].split("/");

            repositoryQueryPartsBuilder.append(getRepositoryQueryPart(i, ownerWithName[0], ownerWithName[1]));
        }

        final String query = "query FavoriteRepositories {\n" +
                "  rateLimit {\n" +
                "    limit\n" +
                "    remaining\n" +
                "    resetAt\n" +
                "  }\n" +
                repositoryQueryPartsBuilder.toString() +
                "}";

        return client.<JsPropertyMap>request(query, JsPropertyMap.of())
                .map(jsPropertyMap -> {
                    final JsArray<Repository> repositoryList = new JsArray<>();

                    for (String key : JsObject.keys(jsPropertyMap)) {
                        if (key.startsWith("_")) {
                            final Repository repository = (Repository) jsPropertyMap.get(key);

                            repositoryList.push(repository);
                        }
                    }

                    return repositoryList;
                });
    }

    public String getRepositoryQueryPart(int index, String owner, String name) {
        return "_" + index + ": repository(owner: \""+ owner + "\", name: \""+ name +"\") {\n" +
                "    nameWithOwner\n" +
                "    description\n" +
                "    primaryLanguage{\n" +
                "      name\n" +
                "      color\n" +
                "    }\n" +
                "    stargazers {\n" +
                "      totalCount\n" +
                "    }\n" +
                "    \n" +
                "    updatedAt\n" +
                "  }\n";
    }
}
