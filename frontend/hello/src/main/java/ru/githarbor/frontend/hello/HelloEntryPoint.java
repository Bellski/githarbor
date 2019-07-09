package ru.githarbor.frontend.hello;

import com.axellience.vuegwt.core.client.VueGWT;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import elemental2.core.Global;
import elemental2.dom.HTMLMetaElement;
import io.reactivex.plugins.RxJavaPlugins;
import jsinterop.base.Js;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.hello.dagger.DaggerHelloDaggerComponent;
import ru.githarbor.shared.User;

import static elemental2.dom.DomGlobal.document;

public class HelloEntryPoint implements EntryPoint {
    @Override
    public void onModuleLoad() {
        RxJavaPlugins.setErrorHandler(throwable -> GWT.log("", throwable));
        VueGWT.initWithoutVueLib();

        final User user = processUser();
        final GitHubGqlClient gitHubGqClient = new GitHubGqlClient(user);

        DaggerHelloDaggerComponent.builder()
                .user(user)
                .gitHubGqlClient(gitHubGqClient)
                .build()
                .helloComponentFactory()
                .create()
                .vue()
                .$mount("#app");
    }

    private User processUser() {

        final HTMLMetaElement meta = Js.cast(document.head.querySelector("[data-user]"));
        final User user = Js.cast(Global.JSON.parse(meta.dataset.get("user")));

        meta.remove();
        return user;
    }
}
