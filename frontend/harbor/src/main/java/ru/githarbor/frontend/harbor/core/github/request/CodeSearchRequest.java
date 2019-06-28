package ru.githarbor.frontend.harbor.core.github.request;

import elemental2.core.Global;
import elemental2.dom.Headers;
import elemental2.dom.RequestInit;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.ReplaySubject;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import ru.githarbor.frontend.github.data.RateLimit;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.intendia.rxgwt2.elemental2.RxElemental2.fromPromise;
import static elemental2.core.JsObject.defineProperty;
import static elemental2.dom.DomGlobal.fetch;
import static jsinterop.base.JsPropertyMap.of;

@Singleton
public class CodeSearchRequest {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class CodeSearchResponse {
        public String pages;
        public double total_count;
        public double limit;
        public double remaining;
        public String reset;
        public CodeSearchItem[] items;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static  class CodeSearchItem {
        public String name;
        public String path;
        public String git_url;
        public double score;
    }

    private final String accessToken;
    private final Repository repository;

    private FileContentRequest fileContentRequest;

    private ReplaySubject<RateLimit> rateLimitObservable = ReplaySubject.create();

    private RateLimit rateLimit;

    @Inject
    public CodeSearchRequest(User user, Repository repository, FileContentRequest fileContentRequest) {
        this.accessToken = user.accessToken;
        this.repository = repository;
        this.fileContentRequest = fileContentRequest;
    }

    public Single<CodeSearchResponse> execute(String path, String query, double page, String extension) {
        return execute(path, query, page, 20, extension);
    }

    public Single<CodeSearchResponse> execute(String path, String query, double page, double perPage, String extension) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder
                .append("q=")
                .append(Global.encodeURIComponent(query));

        if (path != null) {
            queryBuilder
                    .append("+path:")
                    .append(Global.encodeURIComponent(path));
        }

        queryBuilder
                .append("+in:file");

        if (extension != null) {
            queryBuilder
                    .append("+extension:")
                    .append(extension);
        }

        queryBuilder
                .append("+repo:")
                .append(Global.encodeURIComponent(repository.info.nameWithOwner.ownerWithName));

        final Headers headers = new Headers();
        headers.append("Accept", "application/vnd.github.v3.text-match+json");
        headers.append("Authorization", "token " + accessToken);

        final RequestInit requestInit = RequestInit.create();
        requestInit.setHeaders(headers);
        requestInit.setMethod("GET");

        return fromPromise(fetch("https://api.github.com/search/code?" + queryBuilder + "&per_page=" + perPage + "&page=" + page, requestInit))
                .doOnSuccess(response -> {
                    if (response.status == 403) {
                        throw new RuntimeException();
                    }
                })
                .flatMap(response -> fromPromise(response.json())
                        .map(json -> {
                            defineProperty(json, "pages", of("value", response.headers.get("Link")));

                            defineProperty(json, "limit", of("value", response.headers.get("X-RateLimit-Limit")));

                            defineProperty(json, "remaining", of("value", response.headers.get("X-RateLimit-Remaining")));

                            defineProperty(json, "reset", of("value", response.headers.get("X-RateLimit-Reset")));

                            return Js.<CodeSearchResponse>cast(json);
                        }))

                .doOnSuccess(codeSearchResponse -> {
                    final RateLimit rateLimit = new RateLimit();
                    rateLimit.limit = codeSearchResponse.limit;
                    rateLimit.remaining = codeSearchResponse.remaining;
                    rateLimit.resetAt = codeSearchResponse.reset;

                    this.rateLimit = rateLimit;

                    rateLimitObservable.onNext(rateLimit);
                });
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public Observable<RateLimit> onRateLimitUpdate() {
        return rateLimitObservable;
    }
}
