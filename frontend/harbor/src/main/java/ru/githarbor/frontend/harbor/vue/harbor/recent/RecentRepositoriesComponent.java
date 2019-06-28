package ru.githarbor.frontend.harbor.vue.harbor.recent;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import elemental2.core.JsDate;
import elemental2.core.JsNumber;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.harbor.core.rpc.UserManagerRpcClient;
import ru.githarbor.shared.RecentRepository;
import ru.githarbor.shared.User;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;

import static elemental2.dom.DomGlobal.window;

@Component
public class RecentRepositoriesComponent implements IsVueComponent, HasCreated {

    @Inject
    public User user;

    @Inject
    public UserManagerRpcClient userManager;

    @Data
    public boolean loading;

    @Data
    public String input;

    @Data
    public RecentRepository[] repositories;

    public RecentRepository[] allRepositories;

    @Override
    public void created() {

        final Comparator<RecentRepository> comparing = Comparator.comparing(recentRepository -> recentRepository.date);
        Arrays.sort(user.recentRepositories, comparing.reversed());

        allRepositories = user.recentRepositories;
        repositories = allRepositories;
    }

    @JsMethod
    public String formatDate(long date) {
        return new JsDate(new JsNumber(date)).toLocaleDateString() + ":" + new JsDate(new JsNumber(date)).toLocaleTimeString();
    }

    @JsMethod
    public void onNewWindow(String name) {
        window.open("/github/" + name, "_blank");
    }

    @JsMethod
    public void onThisWindow(String name) {
        window.location.setHref("/github/" + name);
    }

    @JsMethod
    public void onDelete(String name) {
        allRepositories = Arrays.stream(allRepositories)
                .filter(repository -> !repository.name.equals(name))
                .toArray(RecentRepository[]::new);

        repositories = allRepositories;

        userManager.deleteRecentRepository(name);
    }
}
