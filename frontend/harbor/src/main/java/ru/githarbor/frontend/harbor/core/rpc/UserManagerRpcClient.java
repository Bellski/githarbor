package ru.githarbor.frontend.harbor.core.rpc;

import ru.githarbor.shared.UiState;
import ru.githarbor.shared.rpc.*;

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

    public void deleteAllRecentRepositories() {
        client.execute("/user/user-manager", new DeleteAllRecentRepositories());
    }

    public void setTheme(boolean dark) {
        client.execute("/user/user-manager", new SetThemeRequest(dark));
    }

    public void updateUiState(UiState uiState) {
        client.execute("/user/user-manager", new CreateUiStateRequest(uiState));
    }
}
