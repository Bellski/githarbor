package ru.githarbor.frontend.fileviewer.core;

import elemental2.dom.Headers;
import elemental2.dom.RequestInit;
import elemental2.promise.Promise;
import io.reactivex.Single;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import static com.intendia.rxgwt2.elemental2.RxElemental2.fromPromise;
import static elemental2.core.Global.JSON;
import static elemental2.dom.DomGlobal.fetch;

public class GitHubGqlClient {

    public static class GraphQLRequestBody {

        @JsProperty
        private Object query;

        @JsProperty
        private JsPropertyMap variables;

        public GraphQLRequestBody(Object query, JsPropertyMap variables) {
            this.query = query;
            this.variables = variables;
        }
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class GraphQLResponse {
        public Object data;
        public Error[] errors;
    }


    private String accessToken;

    public GitHubGqlClient(String accessToken) {
        this.accessToken = accessToken;
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
                    return Js.cast(graphQLResponse.data);
                });
    }
}
