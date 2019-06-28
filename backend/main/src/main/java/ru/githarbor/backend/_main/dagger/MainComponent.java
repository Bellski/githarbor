package ru.githarbor.backend._main.dagger;

import dagger.Component;
import io.javalin.websocket.WsSession;
import ru.githarbor.backend._main.manager.RepositoryTaskManager;
import ru.githarbor.backend._main.manager.UserManager;
import ru.githarbor.backend._main.server.rpc.UserManagerRpcServer;
import ru.githarbor.backend._main.server.ws.RepositoryPathsService;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
@Component(modules = MainDeclarations.class)
public interface MainComponent {
    UserManager userManager();
    RepositoryTaskManager repositoryTaskManager();

    Map<String, WsSession> wsConnections();
    RepositoryPathsService repositoryPathsService();
    UserManagerRpcServer userManagerRpcServer();
}
