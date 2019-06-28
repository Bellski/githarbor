package ru.githarbor.backend._main.dagger;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dagger.Module;
import dagger.Provides;
import io.javalin.websocket.WsSession;
import okhttp3.OkHttpClient;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import ru.githarbor.backend._main.manager.RepositoryPathsManager;
import ru.githarbor.backend._main.manager.RepositoryTaskManager;
import ru.githarbor.backend._main.server.ws.RepositoryPathsService;
import ru.githarbor.repositories.autoupdate.fetch.RepositoryTreeListFetcher;
import ru.githarbor.repositories.autoupdate.manager.RepositoryCacheManager;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Module
public class MainDeclarations {

    @Provides
    @Singleton
    DataSource provideSqlDataSource() {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        config.setUsername("postgres");
        config.setPassword("1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    @Provides
    @Singleton
    DSLContext provideJooq(DataSource ds) {
        return DSL.using(ds, SQLDialect.POSTGRES_10);
    }

    @Provides
    @Singleton
    Map<String, WsSession> provideWsConnections() {
        return new HashMap<>();
    }

    @Provides
    @Singleton
    RepositoryPathsService provideRepositoryPathsService(Map<String, WsSession> wsConnections, RepositoryTaskManager repositoryTasksManager, RepositoryPathsManager repositoryPathsManager, RepositoryCacheManager repositoryCacheManager) {
        return new RepositoryPathsService(wsConnections, repositoryTasksManager, repositoryPathsManager, repositoryCacheManager, new RepositoryTreeListFetcher(new OkHttpClient()));
    }

    @Provides
    @Singleton
    Gson provieGson() {
        return new Gson();
    }
}
