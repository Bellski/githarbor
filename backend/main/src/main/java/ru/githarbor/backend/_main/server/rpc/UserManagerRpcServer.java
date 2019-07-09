package ru.githarbor.backend._main.server.rpc;

import com.google.gson.Gson;
import io.javalin.Context;
import io.javalin.Handler;
import org.jetbrains.annotations.NotNull;
import ru.githarbor.backend._main.Main;
import ru.githarbor.backend._main.manager.UserManager;
import ru.githarbor.shared.rpc.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserManagerRpcServer implements Handler {

    private final UserManager userManager;
    private final Gson gson;

    @Inject
    public UserManagerRpcServer(UserManager userManager, Gson gson) {
        this.userManager = userManager;
        this.gson = gson;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        final UserManagerRequest userManagerRequest = gson.fromJson(ctx.body(), UserManagerRequest.class);

        if (userManagerRequest.methodName.equals(AddFavoriteRepository.class.getName())) {
            final AddFavoriteRepository addFavoriteRepository = gson.fromJson(ctx.body(), AddFavoriteRepository.class);

           userManager.addFavoriteRepository(ctx.sessionAttribute(Main.GH_ID), addFavoriteRepository.name);

           return;
        }

        if (userManagerRequest.methodName.equals(DeleteFavoriteRepository.class.getName())) {
            final DeleteFavoriteRepository deleteFavoriteRepository = gson.fromJson(ctx.body(), DeleteFavoriteRepository.class);

            userManager.deleteFavoriteRepository(ctx.sessionAttribute(Main.GH_ID), deleteFavoriteRepository.name);

            return;
        }

        if (userManagerRequest.methodName.equals(AddRecentRepository.class.getName())) {
            final AddRecentRepository addRecentRepository = gson.fromJson(ctx.body(), AddRecentRepository.class);

            userManager.updateOrAddRecentRepository(ctx.sessionAttribute(Main.GH_ID), addRecentRepository.name, addRecentRepository.timestamp);

            return;
        }

        if (userManagerRequest.methodName.equals(DeleteRecentRepository.class.getName())) {
            final DeleteRecentRepository deleteRecentRepository = gson.fromJson(ctx.body(), DeleteRecentRepository.class);

            userManager.deleteRecentRepository(ctx.sessionAttribute(Main.GH_ID), deleteRecentRepository.name);

            return;
        }

        if (userManagerRequest.methodName.equals(SetThemeRequest.class.getName())) {
            final SetThemeRequest setThemeRequest = gson.fromJson(ctx.body(), SetThemeRequest.class);

            userManager.setTheme(ctx.sessionAttribute(Main.GH_ID), setThemeRequest.dark);

            return;
        }

        if (userManagerRequest.methodName.equals(DeleteAllRecentRepositories.class.getName())) {
            final DeleteAllRecentRepositories deleteAllRecentRepositories = gson.fromJson(ctx.body(), DeleteAllRecentRepositories.class);

            userManager.deleteAllRecentRepositories(ctx.sessionAttribute(Main.GH_ID));

            return;
        }

        if (userManagerRequest.methodName.equals(CreateUiStateRequest.class.getName())) {
            final CreateUiStateRequest createUiStateRequest = gson.fromJson(ctx.body(), CreateUiStateRequest.class);

            final int updateResult = userManager.updateUiState(ctx.sessionAttribute(Main.GH_ID), createUiStateRequest.uiState);

            if (updateResult == 0) {
                userManager.createUiState(ctx.sessionAttribute(Main.GH_ID), createUiStateRequest.uiState);
            }
        }
    }
}
