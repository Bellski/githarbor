package ru.githarbor.backend._main.manager;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.exception.DataAccessException;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jooq.impl.DSL.inline;
import static org.jooq.util.postgres.PostgresDSL.arrayAppend;
import static ru.githarbor.backend.db.tables.RepositoriesCacheTasks.REPOSITORIES_CACHE_TASKS;

@Singleton
public class RepositoryTaskManager {

    private final DSLContext dsl;

    @Inject
    public RepositoryTaskManager(DSLContext dsl) {
        this.dsl = dsl;
    }

    public boolean existsTask(String repoName, String branchName) {
        return dsl.selectOne()
                .from(REPOSITORIES_CACHE_TASKS)
                .where(REPOSITORIES_CACHE_TASKS.NAME.eq(branchName))
                .fetchOne() != null;
    }

    public long createTask(String repoName, String branchName, long userId) {
        try {
            return dsl.insertInto(REPOSITORIES_CACHE_TASKS, REPOSITORIES_CACHE_TASKS.NAME, REPOSITORIES_CACHE_TASKS.USER_ID)
                    .values(repoName + "/" + branchName, userId)
                    .returning(REPOSITORIES_CACHE_TASKS.ID)
                    .fetchOne()
                    .getId();
        } catch (DataAccessException e) {
            if (e.sqlState().equals("23505")) {
                return -1;
            }
        }

        return -1;
    }

    public void deleteTask(String name) {
        dsl.delete(REPOSITORIES_CACHE_TASKS)
                .where(REPOSITORIES_CACHE_TASKS.NAME.eq(name))
                .execute();
    }

    public void deleteTask(long id) {
        dsl.delete(REPOSITORIES_CACHE_TASKS)
                .where(REPOSITORIES_CACHE_TASKS.ID.eq(id))
                .execute();
    }

    public void addSubscriber(String repoName, String branchName, String subscriber) {
        dsl.update(REPOSITORIES_CACHE_TASKS)
                .set(REPOSITORIES_CACHE_TASKS.SUBSCRIBERS, arrayAppend(REPOSITORIES_CACHE_TASKS.SUBSCRIBERS, inline(subscriber)))
                .where(REPOSITORIES_CACHE_TASKS.NAME.eq(repoName + "/" + branchName))
                .execute();
    }

    public String[] getSubscribers(String repoName, String branchName) {
        final Record1<String[]> result = dsl.select(REPOSITORIES_CACHE_TASKS.SUBSCRIBERS)
                .from(REPOSITORIES_CACHE_TASKS)
                .where(REPOSITORIES_CACHE_TASKS.NAME.eq(repoName + "/" + branchName))
                .fetchOne();

        return  ( result != null && result.value1() != null ) ? result.value1() : new String[0];
    }
}
