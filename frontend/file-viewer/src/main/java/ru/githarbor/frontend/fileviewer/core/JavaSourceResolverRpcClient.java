package ru.githarbor.frontend.fileviewer.core;

import io.reactivex.Single;
import jsinterop.base.Js;
import ru.githarbor.shared.JavaSourceMetadataDTO;
import ru.githarbor.shared.rpc.java.ResolveSourceRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JavaSourceResolverRpcClient {

    private final HarborRpcClient client;

    @Inject
    public JavaSourceResolverRpcClient(HarborRpcClient client) {
        this.client = client;
    }

    public Single<JavaSourceMetadataDTO> resolveSource(String ownerWithName, String path) {
        return client.execute("/java-resolver", new ResolveSourceRequest(ownerWithName, path))
                .map(Js::cast);
    }
}
