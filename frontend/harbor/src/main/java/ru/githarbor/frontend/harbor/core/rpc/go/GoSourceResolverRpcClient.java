package ru.githarbor.frontend.harbor.core.rpc.go;

import io.reactivex.Single;
import ru.githarbor.frontend.harbor.core.rpc.HarborRpcClient;
import ru.githarbor.shared.rpc.go.GoResolveSourceRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GoSourceResolverRpcClient {

    private final HarborRpcClient client;

    @Inject
    public GoSourceResolverRpcClient(HarborRpcClient client) {
        this.client = client;
    }

    public Single<GoSourceMetadata> resolveSource(String ownerWithName, String path) {
        return client.execute2("/go-resolver", new GoResolveSourceRequest(ownerWithName, path));
    }
}
