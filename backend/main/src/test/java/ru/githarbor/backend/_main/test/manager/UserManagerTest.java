package ru.githarbor.backend._main.test.manager;

import org.junit.Test;
import ru.githarbor.backend._main.dagger.DaggerMainComponent;
import ru.githarbor.backend._main.dagger.MainComponent;
import ru.githarbor.shared.BranchState;
import ru.githarbor.shared.UiState;
import ru.githarbor.shared.User;

import java.time.Instant;

public class UserManagerTest {


    public void createUser() {
        final MainComponent component = DaggerMainComponent.create();

        component.userManager().createUser(1234);
    }


    public void getUser() {
        final MainComponent component = DaggerMainComponent.create();

        final User user = component.userManager()
                .getUser(1234);

        System.out.println(user);
    }

    public void setTheme() {
        final MainComponent component = DaggerMainComponent.create();
        component.userManager().setTheme(1234, "dark");
    }


    public void addFavoriteRepository() {
        final MainComponent component = DaggerMainComponent.create();
        component.userManager().addFavoriteRepository(1234, "vue/vuejs");
    }

    public void deleteFavoriteRepository() {
        final MainComponent component = DaggerMainComponent.create();
        component.userManager().deleteFavoriteRepository(1234, "vue/vuejs");
    }

    public void addRecentRepository() {
        final MainComponent component = DaggerMainComponent.create();
        component.userManager().updateOrAddRecentRepository(1234, "vue/vuejs", Instant.now().toEpochMilli());
    }

    public void deleteRecentRepository() {
        final MainComponent component = DaggerMainComponent.create();
        component.userManager().deleteRecentRepository(1234, "vue/vuejs");
    }


    @Test
    public void createUiState() {
        final BranchState branchState = new BranchState();
        branchState.name = "master";

        final UiState uiState = new UiState();
        uiState.name = "vuejs/vue";
        uiState.sideBarTabIndex = 1;
        uiState.sideBarTabContentWidth = 20;
        uiState.sideBarContentWidth = 80;
        uiState.currentBranch = "master100";
        uiState.branches = new BranchState[] {branchState};

        final MainComponent component = DaggerMainComponent.create();

        component.userManager().selectUiState(1234, "vuejs/vue");
    }
}
