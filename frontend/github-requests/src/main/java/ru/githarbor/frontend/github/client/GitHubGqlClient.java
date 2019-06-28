package ru.githarbor.frontend.github.client;

import elemental2.dom.Headers;
import elemental2.dom.RequestInit;
import elemental2.promise.Promise;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.ReplaySubject;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ru.githarbor.frontend.github.data.GitHubGraphQLException;
import ru.githarbor.frontend.github.data.GraphQLRequestBody;
import ru.githarbor.frontend.github.data.GraphQLResponse;
import ru.githarbor.frontend.github.data.RateLimit;
import ru.githarbor.shared.User;

import static com.intendia.rxgwt2.elemental2.RxElemental2.fromPromise;
import static elemental2.core.Global.JSON;
import static elemental2.dom.DomGlobal.fetch;

public class GitHubGqlClient {

    private String accessToken;

    private ReplaySubject<RateLimit> rateLimitObservable = ReplaySubject.create();

    private RateLimit rateLimit;

    public GitHubGqlClient(User user) {
        this.accessToken = user.accessToken;
    }

    public <T> Single<T> request(String query, JsPropertyMap values) {
        final Headers headers = new Headers();
        headers.append("Accept", "application/json");
        headers.append("Authorization", "token " + accessToken);

        final RequestInit requestInit = RequestInit.create();
        requestInit.setHeaders(headers);
        requestInit.setMethod("POST");
        requestInit.setBody(JSON.stringify(new GraphQLRequestBody(query, values)));

        return fromPromise(fetch("https://api.github.com/graphql", requestInit))
                .flatMap(response -> fromPromise(Js.<Promise<GraphQLResponse>>cast(response.json())))
                .map(graphQLResponse -> {
                    this.rateLimit = graphQLResponse.data.rateLimit;

                    rateLimitObservable.onNext(graphQLResponse.data.rateLimit);

                    if (graphQLResponse.errors != null) {
                        throw new GitHubGraphQLException(graphQLResponse.errors);
                    }

                    return Js.cast(graphQLResponse.data);
                });
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public Observable<RateLimit> onRateLimitUpdate() {
        return rateLimitObservable;
    }
}
