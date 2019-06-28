package ru.githarbor.frontend.harbor.core.github.request;

import elemental2.dom.Headers;
import elemental2.dom.RequestInit;
import elemental2.promise.Promise;
import io.reactivex.Single;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import ru.githarbor.frontend.harbor.core.github.core.Repository;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Stream;

import static com.intendia.rxgwt2.elemental2.RxElemental2.fromPromise;
import static elemental2.dom.DomGlobal.fetch;

@Singleton
public class CommitFilesRequest {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class CommitFilesResponse {
        public String sha;
        public CommitFile[] files;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class CommitFile {
        public String filename;
        public String status;
        public String branchSha;
    }

    private final String accessToken;
    private final Repository repository;

    @Inject
    public CommitFilesRequest(User user, Repository repository) {
        this.accessToken = user.accessToken;
        this.repository = repository;
    }

    public Single<CommitFile[]> execute(String commitOid) {
        final Headers headers = new Headers();
        headers.append("Authorization", "token " + accessToken);

        final RequestInit requestInit = RequestInit.create();
        requestInit.setHeaders(headers);
        requestInit.setMethod("GET");

        return fromPromise(fetch("https://api.github.com/repos/" + repository.info.nameWithOwner.ownerWithName + "/commits/" + commitOid, requestInit))
                .flatMap(response -> fromPromise(Js.<Promise<CommitFilesResponse>>cast(response.json()))
                        .map(commitFilesResponse -> Stream.of(commitFilesResponse.files)
                                .peek(commitFile -> commitFile.branchSha = commitFilesResponse.sha)
                                .toArray(CommitFile[]::new)));
    }
}
