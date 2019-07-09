package ru.githarbor.frontend.hello.vue.repository;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import ru.githarbor.frontend.hello.vue.favorite.FavoriteRepositoriesComponent;
import ru.githarbor.frontend.hello.vue.recent.RecentRepositoriesComponent;
import ru.githarbor.shared.FavoriteRepository;
import ru.githarbor.shared.User;

import javax.inject.Inject;

@Component(components = {
        RecentRepositoriesComponent.class,
        FavoriteRepositoriesComponent.class
})
public class RepositoriesViewComponent implements IsVueComponent, HasCreated {

    @Inject
    public User user;

    @Data
    public String tab = "recent";

    @Computed
    public boolean getIsTier1User() {
        return user.tier1Backer;
    }

    @Override
    public void created() {
        if (getIsTier1User()) {
            tab = "favorites";
        }
    }
}
