package ru.githarbor.frontend.hello.dagger;

import dagger.BindsInstance;
import dagger.Component;
import ru.githarbor.frontend.github.client.GitHubGqlClient;
import ru.githarbor.frontend.hello.vue.HelloComponentFactory;
import ru.githarbor.shared.User;

import javax.inject.Singleton;

@Singleton
@Component
public interface HelloDaggerComponent {
    HelloComponentFactory helloComponentFactory();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder user(User user);

        @BindsInstance
        Builder gitHubGqlClient(GitHubGqlClient client);

        HelloDaggerComponent build();
    }
}
