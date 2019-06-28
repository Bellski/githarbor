package ru.githarbor.frontend.harbor.core.rpc;

import ru.githarbor.shared.rpc.AddFavoriteRepository;
import ru.githarbor.shared.rpc.AddRecentRepository;
import ru.githarbor.shared.rpc.DeleteFavoriteRepository;
import ru.githarbor.shared.rpc.DeleteRecentRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserManagerRpcClient {

    private final HarborRpcClient client;

    @Inject
    public UserManagerRpcClient(HarborRpcClient client) {
        this.client = client;
    }

    public void addFavoriteRepository(String name) {
        client.execute("/user/user-manager", new AddFavoriteRepository(name));
    }

    public void deleteFavoriteRepository(String name) {
        client.execute("/user/user-manager", new DeleteFavoriteRepository(name));
    }

    public void addRecentRepository(String name, long timestamp) {
        client.execute("/user/user-manager", new AddRecentRepository(name, timestamp));
    }

    public void deleteRecentRepository(String name) {
        client.execute("/user/user-manager", new DeleteRecentRepository(name));
    }
}
