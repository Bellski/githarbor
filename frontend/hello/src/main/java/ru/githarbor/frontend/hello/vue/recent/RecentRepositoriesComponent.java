package ru.githarbor.frontend.hello.vue.recent;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import elemental2.core.JsDate;
import elemental2.core.JsNumber;
import jsinterop.annotations.JsMethod;
import ru.githarbor.frontend.hello.rpc.UserManagerRpcClient;
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

    @Data
    public double currentOverIndex = -1;

    @Override
    public void created() {

        final Comparator<RecentRepository> comparing = Comparator.comparing(recentRepository -> recentRepository.date);
        Arrays.sort(user.recentRepositories, comparing.reversed());

        allRepositories = user.recentRepositories;
        repositories = allRepositories;

        vue().$watch(() -> input, (newInput, oldInput) -> {
            if (newInput.isEmpty()) {
                repositories = allRepositories;

                return;
            }

            repositories = Arrays.stream(allRepositories).filter(repository -> repository.name.contains(newInput))
                    .toArray(RecentRepository[]::new);
        });
    }

    @JsMethod
    public String formatDate(long date) {
        return new JsDate(new JsNumber(date)).toLocaleDateString() + ":" + new JsDate(new JsNumber(date)).toLocaleTimeString();
    }

    @JsMethod
    public void onNewWindow(String name) {
        window.open("/" + name, "_blank");
    }

    @JsMethod
    public void onThisWindow(String name) {
        window.location.setHref("/" + name);
    }

    @JsMethod
    public void onDelete(String name) {
        allRepositories = Arrays.stream(allRepositories)
                .filter(repository -> !repository.name.equals(name))
                .toArray(RecentRepository[]::new);

        repositories = allRepositories;

        userManager.deleteRecentRepository(name);
    }

    @JsMethod
    public void onDeleteAll() {
        allRepositories = new RecentRepository[0];
        repositories = new RecentRepository[0];

        userManager.deleteAllRecentRepositories();
    }

    @JsMethod
    public void onMouseOver(double index) {
        currentOverIndex = index;
    }

    @JsMethod
    public void onMouseOut() {
        currentOverIndex = -1;
    }
}
