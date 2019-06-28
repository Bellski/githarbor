package ru.githarbor.repositories.autoupdate.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jooq.*;
import org.jooq.impl.DSL;
import ru.githarbor.repositories.autoupdate.data.RepositoryCache;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import static org.jooq.impl.DSL.*;
import static ru.githarbor.backend.db.tables.BranchesPaths.BRANCHES_PATHS;
import static ru.githarbor.backend.db.tables.RepositoriesCache.REPOSITORIES_CACHE;

@Singleton
public class RepositoryCacheManager {

    private final DSLContext dsl;

    @Inject
    public RepositoryCacheManager(DSLContext dsl) {
        this.dsl = dsl;
    }

    public RepositoryCache getRepositoryCache(String name) {
        return dsl.select(REPOSITORIES_CACHE.ID, REPOSITORIES_CACHE.NAME, REPOSITORIES_CACHE.BRANCHES)
                .from(REPOSITORIES_CACHE)
                .where(REPOSITORIES_CACHE.NAME.eq(name))
                .fetchOne(record -> {
                    final RepositoryCache repositoryCache = new RepositoryCache();
                    repositoryCache.id = record.component1();
                    repositoryCache.name = record.component2();
                    repositoryCache.branches = new Gson().fromJson(record.component3(), RepositoryCache.Branch[].class);

                    return repositoryCache;
                });
    }

    public void createAutoUpdateRepositoryCache(String name, String branch, long lastCommit, byte[] gzipedPaths) throws IOException {
        createRepositoryCache(name, branch, lastCommit, gzipedPaths, true);
    }

    public void createRepositoryCache(String name, String branch, long lastCommit, byte[] gzipedPaths, boolean autoUpdate) throws IOException {
        final JsonObject jsonElement = new JsonObject();
        jsonElement.addProperty("name", branch);
        jsonElement.addProperty("lastCommit", lastCommit);
        jsonElement.addProperty("autoUpdate", autoUpdate);

        final JsonArray branches = new JsonArray();
        branches.add(jsonElement);

        dsl.transaction((configuration -> {
            DSL.using(configuration)
                    .insertInto(REPOSITORIES_CACHE, REPOSITORIES_CACHE.NAME, REPOSITORIES_CACHE.BRANCHES)
                    .values(name, branches)
                    .execute();


            DSL.using(configuration)
                    .insertInto(BRANCHES_PATHS, BRANCHES_PATHS.NAME, BRANCHES_PATHS.PATHS)
                    .values(name + "/" + branch, gzipedPaths)
                    .execute();
        }));
    }

    public void addBranchPaths(String repoName, String branch, long lastCommit, byte[] gzipedPaths) {
        final JsonObject jsonElement = new JsonObject();
        jsonElement.addProperty("name", branch);
        jsonElement.addProperty("lastCommit", lastCommit);
        jsonElement.addProperty("autoUpdate", true);

        dsl.transaction((configuration -> {
            DSL.using(configuration)
                    .update(REPOSITORIES_CACHE)
                    .set(REPOSITORIES_CACHE.BRANCHES, field("{0} || {1}::jsonb", REPOSITORIES_CACHE.BRANCHES.getDataType(), REPOSITORIES_CACHE.BRANCHES, jsonElement.toString()))
                    .where(REPOSITORIES_CACHE.NAME.eq(repoName))
                    .execute();

            DSL.using(configuration)
                    .insertInto(BRANCHES_PATHS, BRANCHES_PATHS.NAME, BRANCHES_PATHS.PATHS)
                    .values(repoName + "/" + branch, gzipedPaths)
                    .execute();
        }));
    }

    public void updateBranchPaths(String repoName, String branch, long lastCommit, byte[] gzipedPaths) {
        final JsonObject jsonElement = new JsonObject();
        jsonElement.addProperty("name", branch);
        jsonElement.addProperty("lastCommit", lastCommit);
        jsonElement.addProperty("autoUpdate", true);

        final JsonArray branches = new JsonArray();
        branches.add(jsonElement);

        dsl.transaction((configuration -> {
            final Field<JsonElement> branchesColumn = field("branch", REPOSITORIES_CACHE.BRANCHES.getType());
            final Field<Integer> indexColumn = field("index - 1", Integer.class).as("index");
            final Table<Record> jsonb_array_elements = table("jsonb_array_elements({0}) WITH ORDINALITY arr({1}, {2})", REPOSITORIES_CACHE.BRANCHES, branchesColumn, indexColumn);
            final Field<JsonElement> jsonb_set = field(
                    "jsonb_set({0}, ('{' || index_of_branch.index || '}')::text[], index_of_branch.branch || {1}::jsonb, false)", REPOSITORIES_CACHE.BRANCHES.getDataType(),
                    REPOSITORIES_CACHE.BRANCHES,
                    jsonElement.toString()
            );

            final CommonTableExpression<Record2<JsonElement, Integer>> index_of_branch = name("index_of_branch").as(
                    select(branchesColumn, indexColumn).from(REPOSITORIES_CACHE, jsonb_array_elements)
                            .where(REPOSITORIES_CACHE.NAME.eq(repoName).and(condition("{0} ->> 'name' = {1}", branchesColumn, branch))));

            DSL.using(configuration)
                    .with(index_of_branch)
                    .update(REPOSITORIES_CACHE)
                    .set(REPOSITORIES_CACHE.BRANCHES, jsonb_set)
                    .from(index_of_branch)
                    .where(REPOSITORIES_CACHE.NAME.eq(repoName))
                    .execute();

            DSL.using(configuration)
                    .update(BRANCHES_PATHS)
                    .set(BRANCHES_PATHS.PATHS, gzipedPaths)
                    .where(BRANCHES_PATHS.NAME.eq(repoName + "/" + branch))
                    .execute();

        }));
    }

    public boolean branchIsOutOfDate(String repoName, String branch, long lastCommit) {
        final Table<Record> jsonb_array_elements = table("jsonb_array_elements(branches) WITH ORDINALITY arr(branch_to_update, index)", REPOSITORIES_CACHE.BRANCHES);

        return dsl.selectOne()
                .from(REPOSITORIES_CACHE, jsonb_array_elements)
                .where(REPOSITORIES_CACHE.NAME.eq(repoName).and(condition("branch_to_update ->> 'name' = {0} AND branch_to_update ->> 'lastCommit' = {1}", branch, String.valueOf(lastCommit))))
                .fetchOne() == null;
    }
}
