package ru.githarbor.backend._main.manager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jooq.*;
import org.jooq.util.postgres.PostgresDSL;
import ru.githarbor.backend.db.tables.records.UsersRecord;
import ru.githarbor.shared.RecentRepository;
import ru.githarbor.shared.UiState;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jooq.impl.DSL.*;
import static ru.githarbor.backend.db.tables.Users.USERS;


@Singleton
public class UserManager {

    private final DSLContext dsl;

    @Inject
    public UserManager(DSLContext dsl) {
        this.dsl = dsl;
    }

    public User getUser(long ghId) {
        final Field<JsonElement> recent = field("{0}->'repositories'", USERS.RECENT_REPOSITORIES.getType(), USERS.RECENT_REPOSITORIES).as("recent_repositories");

        return dsl.select(USERS.ID, USERS.DARK_THEME, USERS.TIER2_BACKER, USERS.TIER1_BACKER, USERS.FAVORITE_REPOSITORIES, recent)
                .from(USERS)
                .where(USERS.ID.eq(ghId))
                .fetchOne(record -> {
                    final User user = new User();
                    user.id = record.get(USERS.ID);
                    user.darkTheme = record.get(USERS.DARK_THEME);
                    user.tier2Backer = record.get(USERS.TIER2_BACKER);
                    user.tier1Backer = record.get(USERS.TIER1_BACKER);
                    user.favoriteRepositories = record.get(USERS.FAVORITE_REPOSITORIES);
                    user.recentRepositories = new Gson().fromJson(record.get(USERS.RECENT_REPOSITORIES), RecentRepository[].class);

                    return user;
                });
    }

    public void createUser(long ghId) {
        dsl.insertInto(USERS, USERS.ID)
                .values(ghId)
                .execute();
    }

    public boolean isExists(long ghId) {
        return dsl.fetchExists(selectOne().from(USERS).where(USERS.ID.eq(ghId)));
    }

    public void setTheme(long id, boolean dark) {
        dsl.update(USERS)
                .set(USERS.DARK_THEME, dark)
                .where(USERS.ID.eq(id))
                .execute();
    }

    public void setIsTier2Backer(long userId) {
        dsl.update(USERS)
                .set(USERS.TIER2_BACKER, true)
                .where(USERS.ID.eq(userId))
                .execute();
    }

    public void setIsTier1Backer(long userId) {
        dsl.update(USERS)
                .set(USERS.TIER1_BACKER, true)
                .where(USERS.ID.eq(userId))
                .execute();
    }

    public String[] getFavoriteRepositories(long userId) {
        final String[] result = dsl.select(USERS.FAVORITE_REPOSITORIES)
                .from(USERS)
                .where(USERS.ID.eq(userId))
                .fetchOne().value1();

        return result != null ? result : new String[0];
    }

    public void addFavoriteRepository(long userId, String name) {
        dsl.update(USERS)
                .set(USERS.FAVORITE_REPOSITORIES, PostgresDSL.arrayAppend(USERS.FAVORITE_REPOSITORIES, inline(name)))
                .where(USERS.ID.eq(userId))
                .execute();
    }

    public void deleteFavoriteRepository(long userId, String name) {
        dsl.update(USERS)
                .set(USERS.FAVORITE_REPOSITORIES, PostgresDSL.arrayRemove(USERS.FAVORITE_REPOSITORIES, inline(name)))
                .where(USERS.ID.eq(userId))
                .execute();
    }

    public void updateOrAddRecentRepository(long userId, String name, long timestamp) {
        final Field<JsonElement> recent_repository = field("recent_repository", USERS.RECENT_REPOSITORIES.getType());
        final Field<Integer> index = field("index - 1", Integer.class).as("index");
        final Table<Record> jsonb_array_elements = table("jsonb_array_elements({0}->'repositories') WITH ORDINALITY arr({1}, {2})", USERS.RECENT_REPOSITORIES, recent_repository, index);


        final CommonTableExpression<Record1<Integer>> index_of_repo = name("index_of_repo").as(
                select(index)
                        .from(USERS, jsonb_array_elements)
                        .where(condition("{0} ->> 'name' = {1}", recent_repository, name).and(USERS.ID.eq(userId)))
        );

        final Field<JsonElement> jsonb_set = field(
                "jsonb_set({0}, ('{repositories,' || index_of_repo.index || ',date}')::text[], {1}::jsonb, false)", USERS.RECENT_REPOSITORIES.getDataType(),
                USERS.RECENT_REPOSITORIES,
                String.valueOf(timestamp)
        );

        final int updateResult = dsl
                .with(index_of_repo)
                .update(USERS)
                .set(USERS.RECENT_REPOSITORIES, jsonb_set)
                .from(index_of_repo)
                .where(USERS.ID.eq(userId))
                .execute();

        if (updateResult == 0) {
            final JsonObject repository = new JsonObject();
            repository.addProperty("name", name);
            repository.addProperty("date", timestamp);

            dsl.update(USERS)
                    .set(
                            USERS.RECENT_REPOSITORIES,
                            field(
                                    "jsonb_set(COALESCE({0}, '{}')::jsonb, '{repositories}', COALESCE({0}->'repositories', '[]')::jsonb || {1}::jsonb)",
                                    USERS.RECENT_REPOSITORIES.getType(),
                                    USERS.RECENT_REPOSITORIES,
                                    repository.toString()
                            )
                    ).where(USERS.ID.eq(userId))
                    .execute();
        }
    }

    public void deleteRecentRepository(long userId, String name) {
        final Field<Integer> indexField = field("index - 1", Integer.class).as("index");

        final CommonTableExpression<Record1<Integer>> with = name("index_of_repo").as(
                select(indexField)
                        .from(
                                USERS,
                                table(
                                        "jsonb_array_elements({0}->'repositories') WITH ORDINALITY arr(recent_repository, index)",
                                        USERS.RECENT_REPOSITORIES
                                )
                        )
                        .where(condition("recent_repository->> 'name' = {0}", name).and(USERS.ID.eq(userId)))
        );

        final UpdateConditionStep<UsersRecord> query = dsl
                .with(with)
                .update(USERS)
                .set(
                        USERS.RECENT_REPOSITORIES,
                        field(
                                "jsonb_set({0}, '{repositories}', ({0} -> 'repositories') - index::integer)",
                                USERS.RECENT_REPOSITORIES.getType(),
                                USERS.RECENT_REPOSITORIES
                        )
                )
                .from(with)
                .where(USERS.ID.eq(userId));

        query.execute();
    }

    public void deleteAllRecentRepositories(long userId) {
        dsl.update(USERS)
                .set(USERS.RECENT_REPOSITORIES, new JsonObject())
                .where(USERS.ID.eq(userId))
                .execute();
    }

    public void createUiState(long userId, UiState uiState) {
        final Field<JsonElement> concat = field(
                "COALESCE({0}, '[]') || {1}::jsonb",
                USERS.REPOSITORIES_UI_STATE.getDataType(),
                USERS.REPOSITORIES_UI_STATE,
                new Gson().toJson(uiState)
        );

        dsl.update(USERS)
                .set(USERS.REPOSITORIES_UI_STATE, concat)
                .where(USERS.ID.eq(userId))
                .execute();
    }

    public int updateUiState(long userId, UiState uiState) {
        final Field<JsonElement> repository_ui_state = field("repository_ui_state", USERS.REPOSITORIES_UI_STATE.getType());
        final Field<Integer> index = field("index - 1", Integer.class).as("index");
        final Table<Record> jsonb_array_elements = table("jsonb_array_elements({0}) WITH ORDINALITY arr({1}, {2})", USERS.REPOSITORIES_UI_STATE, repository_ui_state, index);
        final Field<JsonElement> jsonb_set = field(
                "jsonb_set({0}, ('{' || index_of_repo.index || '}')::text[], index_of_repo.repository_ui_state || {1}::jsonb, false)", USERS.REPOSITORIES_UI_STATE.getDataType(),
                USERS.REPOSITORIES_UI_STATE,
                new Gson().toJson(uiState)
        );


        final CommonTableExpression<Record2<JsonElement, Integer>> index_of_repo = name("index_of_repo")
                .as(select(repository_ui_state, index).from(USERS, jsonb_array_elements)
                        .where(condition("{0} ->> 'name' = {1}", repository_ui_state, uiState.name).and(USERS.ID.eq(userId))));

        final UpdateConditionStep<UsersRecord> query = dsl
                .with(index_of_repo)
                .update(USERS)
                .set(USERS.REPOSITORIES_UI_STATE, jsonb_set)
                .from(index_of_repo)
                .where(USERS.ID.eq(userId));

        return query.execute();
    }

    public UiState getUiState(long userId, String name) {
        final Field<JsonElement> repository_ui_state = field("repository_ui_state", USERS.REPOSITORIES_UI_STATE.getType());
        final Field<Integer> index = field("index - 1", Integer.class).as("index");
        final Table<Record> jsonb_array_elements = table("jsonb_array_elements({0}) WITH ORDINALITY arr({1}, {2})", USERS.REPOSITORIES_UI_STATE, repository_ui_state, index);

        final SelectConditionStep<Record2<JsonElement, Integer>> query = dsl.select(repository_ui_state, index)
                .from(USERS, jsonb_array_elements)
                .where(condition("{0} ->> 'name' = {1}", repository_ui_state, name).and(USERS.ID.eq(userId)));

        final Record2<JsonElement, Integer> result = query.fetchOne();

        if (result == null) {
            return null;
        }

        return new Gson().fromJson(result.get(repository_ui_state), UiState.class);
    }
}
