package ru.githarbor.backend._main.manager;

import org.jooq.DSLContext;
import org.jooq.Record1;

import javax.inject.Inject;
import javax.inject.Singleton;

import static ru.githarbor.backend.db.tables.BranchesPaths.BRANCHES_PATHS;

@Singleton
public class RepositoryPathsManager {

    private DSLContext dsl;

    @Inject
    public RepositoryPathsManager(DSLContext dsl) {
        this.dsl = dsl;
    }

    public byte[] getPaths(String repoName, String branchName) {
        final Record1<byte[]> result = dsl.select(BRANCHES_PATHS.PATHS)
                .from(BRANCHES_PATHS)
                .where(BRANCHES_PATHS.NAME.eq(repoName + "/" + branchName))
                .fetchOne();

        return result != null ? result.value1() : new byte[0];
    }
}
