package ru.githarbor.repositories.autoupdate.test;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import ru.githarbor.repositories.autoupdate.manager.RepositoryCacheManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ToCacheRepositoriesFetcherTest {

    public void fetchRepositoriesTst() throws SQLException {
        final String userName = "githarbor";
        final String password = "1";
        final String url = "jdbc:postgresql://localhost:5432/githarbor";

        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
            final DSLContext dsl = DSL.using(conn, SQLDialect.POSTGRES_10);

            final RepositoryCacheManager repositoryCacheManager = new RepositoryCacheManager(dsl);

            System.out.println(repositoryCacheManager.branchIsOutOfDate("nodejs/node", "master", 123456));
        }
    }

    public void getRepositoryCache() throws SQLException {
        final String userName = "githarbor";
        final String password = "1";
        final String url = "jdbc:postgresql://localhost:5432/githarbor";

        try (Connection conn = DriverManager.getConnection(url, userName, password)) {
            final DSLContext dsl = DSL.using(conn, SQLDialect.POSTGRES_10);

            final RepositoryCacheManager repositoryCacheManager = new RepositoryCacheManager(dsl);

            System.out.println(repositoryCacheManager.getRepositoryCache("torvalds/linux"));
        }
    }
}
