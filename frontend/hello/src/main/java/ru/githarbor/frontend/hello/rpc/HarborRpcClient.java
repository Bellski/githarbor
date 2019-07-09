package ru.githarbor.frontend.hello.rpc;

import elemental2.dom.DomGlobal;
import elemental2.dom.Headers;
import elemental2.dom.RequestInit;
import elemental2.promise.Promise;
import io.reactivex.Single;
import jsinterop.base.Js;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.intendia.rxgwt2.elemental2.RxElemental2.fromPromise;
import static elemental2.core.Global.JSON;

@Singleton
public class HarborRpcClient {

    @Inject
    public HarborRpcClient() {
    }

    public Single<Object> execute(String path, Object request) {
        final Headers headers = new Headers();
        headers.append("Accept", "application/json");

        final RequestInit requestInit = RequestInit.create();
        requestInit.setHeaders(headers);
        requestInit.setMethod("POST");
        requestInit.setBody(JSON.stringify(request));

        return fromPromise(DomGlobal.fetch(path, requestInit))
                .flatMap(response -> fromPromise(Js.<Promise<Object>>cast(response.json())));
    }
}
